package com.asyscraft.community_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.CategoryMemberListAdapter
import com.asyscraft.community_module.databinding.ActivityFindPeopleBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.GetUserByCategoryRequest
import com.careavatar.core_model.GetUserByCategoryResponse
import com.careavatar.core_model.SendRequestModelRequestBody
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.setupSearchFilter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class FindPeopleActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityFindPeopleBinding
    private lateinit var mMap: GoogleMap
    private val viewModel: SocialMeetViewmodel by viewModels()
    private lateinit var adapter: CategoryMemberListAdapter
    private val memberList = mutableListOf<GetUserByCategoryResponse.User>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userId: String? = null
    private var locationMarker: Marker? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var filterActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        lifecycleScope.launch {
            userId = userPref.userId.first()
        }

        binding.backbtn.setOnClickListener { finish() }
        binding.filterimage.setOnClickListener {
            val intent = Intent(this, PeopleFilterActivity::class.java)
            filterActivityLauncher.launch(intent)
        }

        hitCategoryData()
        setRecyclerview()
        observer()
        setupSearchFilter()
        filterPeople()

        // Setup map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun filterPeople() {
        filterActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val categoryId = data?.getStringExtra("categoryId").toString()
                val distance = data?.getIntExtra("distance", 10) ?: 10
                recentJoinMember(categoryId, distance)

            }
        }
    }


    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            recreate()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun placeMarker(latLng: LatLng) {
        val icon = BitmapDescriptorFactory.fromResource(com.careavatar.core_ui.R.drawable.ic_location)

        if (locationMarker == null) {
            locationMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Selected Location")
                    .icon(icon)
            )
        } else {
            locationMarker?.position = latLng
        }
    }

    private fun setupSearchFilter() {

        binding.includeSearch.etSearch.setupSearchFilter(
            memberList,
            filterCondition = { item, query ->
                item.name?.contains(query, ignoreCase = true) == true
            }
        ) { filteredList ->
            adapter.updateList(filteredList.toMutableList())
        }

    }


    private fun setRecyclerview() {
        // Sort the list
        val sortedList = memberList.sortedWith(compareBy {
            when (it.status?.lowercase()) {
                "approved" -> 0
                "pending" -> 1
                else -> 2
            }
        })
        adapter = CategoryMemberListAdapter(
            this,
            userId,
            sortedList.toMutableList(),
            onItemClick = { selectedUser, type ->
                if (type == 1) {
                    startActivity(
                        Intent(this, ChatDetailsActivity::class.java)
                            .putExtra("id", selectedUser._id)
                            .putExtra("username", selectedUser.name)
                            .putExtra("mobilenumber", "")
                            .putExtra("status", selectedUser.status)
                            .putExtra("userimage", selectedUser.avatar)
                    )
                } else {
                    val request = SendRequestModelRequestBody(selectedUser._id)
                    hitSendRequest(request)
                }
            }
        )

        binding.recentJoinRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.recentJoinRecyclerview.adapter = adapter
    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.categoryListResponse) {
            if (it.status) {
                val id = it.categories[0].id
                recentJoinMember(id, 30)
            }
        }

        collectApiResultOnStarted(viewModel.getUserByCategoryResponse) { it ->
            memberList.clear()
            it.users?.let { memberList.addAll(it) }

            // Sort the list
            val sortedList = memberList.sortedWith(compareBy {
                when (it.status?.lowercase()) {
                    "approved" -> 0
                    "pending" -> 1
                    else -> 2
                }
            })

            memberList.addAll(sortedList)

            adapter.updateList(memberList)
            adapter.notifyDataSetChanged()

        }
    }

    private fun hitCategoryData() {
        val request = GetCategoryRquest("50")
        launchIfInternetAvailable {
            viewModel.hitGetCategoryList(request)
        }
    }

    private fun hitSendRequest(request: SendRequestModelRequestBody) {

        launchIfInternetAvailable {
            viewModel.hitSendRequest(request)
        }
    }


    private fun recentJoinMember(id: String, distance: Int) {

        val request = GetUserByCategoryRequest(id, distance)
        launchIfInternetAvailable {
            viewModel.hitGetUserByCategory(request)

        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            googleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    placeMarker(latLng)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    updateAddress(latLng)

                }
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }

        // When user taps on map, move the marker
        googleMap.setOnMapClickListener { latLng ->
            placeMarker(latLng)
            updateAddress(latLng)
        }

    }

    private fun updateAddress(latLng: LatLng) {
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
