package com.careavatar.core_ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.careavatar.core_ui.databinding.ActivityAddressPickerBinding
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.toString

@AndroidEntryPoint
class AddressPickerActivity : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var binding: ActivityAddressPickerBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var locationMarker: Marker? = null
    private lateinit var placesClient: PlacesClient

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)

        binding.backbutton.setOnClickListener {
            finish()
        }

        geocoder = Geocoder(this, Locale.getDefault())

        binding.backbutton.setOnClickListener { finish() }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.nextbtn.setOnClickListener {
            val center = googleMap.cameraPosition.target
            val address = binding.editAddress.text.toString()

            val resultIntent = Intent().apply {
                putExtra("selected_address", address)
                putExtra("selected_latitude", center.latitude)
                putExtra("selected_longitude", center.longitude)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        setupSearchListener()

        binding.nextbtn.setOnClickListener {
            val center = googleMap.cameraPosition.target
            val address = binding.editAddress.text.toString()

            val resultIntent = Intent().apply {
                putExtra("selected_address", address)
                putExtra("selected_latitude", center.latitude)
                putExtra("selected_longitude", center.longitude)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }


    }

    private fun setupSearchListener() {
        val searchBox = binding.etSearch

        val token = AutocompleteSessionToken.newInstance()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        searchBox.setAdapter(adapter)

        searchBox.addTextChangedListener { editable ->
            val query = editable.toString()
            if (query.length > 2) {  // Start searching after 3 characters
                coroutineScope.launch(Dispatchers.IO) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(query)
                        .build()

                    val response = placesClient.findAutocompletePredictions(request).await()
                    val predictions = response.autocompletePredictions

                    withContext(Dispatchers.Main) {
                        adapter.clear()
                        adapter.addAll(predictions.map { it.getFullText(null).toString() })
                        adapter.notifyDataSetChanged()

                        searchBox.setOnItemClickListener { _, _, position, _ ->
                            val selectedPrediction = predictions[position]
                            moveCameraToPrediction(selectedPrediction)
                        }
                    }
                }
            }
        }
    }

    private fun moveCameraToPrediction(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId
        val geocoder = Geocoder(this)
        coroutineScope.launch(Dispatchers.IO) {
            val addressList = geocoder.getFromLocationName(prediction.getFullText(null).toString(), 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                withContext(Dispatchers.Main) {
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(latLng).title(prediction.getFullText(null).toString()))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun placeMarker(latLng: LatLng) {
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_location)

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


    private fun updateAddress(latLng: LatLng) {
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.editAddress.setText(address)
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
}