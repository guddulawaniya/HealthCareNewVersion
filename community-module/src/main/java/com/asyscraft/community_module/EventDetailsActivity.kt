package com.asyscraft.community_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ImageAdapter
import com.asyscraft.community_module.databinding.ActivityEventDetailsBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.bumptech.glide.Glide
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable1
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
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EventDetailsActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var imageAdapter: ImageAdapter
    private val viewmodel: SocialMeetViewmodel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private var locationMarker: Marker? = null
    private var eventLatLng: LatLng? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.tvTitle.text = "Event Details"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fetchEventDetails()
        observer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observer() {
        collectApiResultOnStarted(viewmodel.getEventDetailResponse) { it ->
            if (it.success) {
                val event = it.data

                // Load main image
                if (event.attachment.isNotEmpty()) {
                    Glide.with(this)
                        .load(Constants.IMAGE_BASEURL + event.attachment[0])
                        .placeholder(R.drawable.logo)
                        .into(binding.itemImageView)
                }

                setupRecyclerview(event.attachment.toMutableList())

                binding.titleText.text = event.title.toString()
                binding.descriptionTextView.text = event.description
                binding.eventdate.text = formatDateToReadable1(event.eventDate ?: "")
                binding.eventDuration.text = calculateEndtime(event.eventTime ?: "", "50")

                if (event.eventMode == "online") {
                    binding.joinbtn.buttonNext.setOnClickListener {
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra("url", event.eventLink)
                        startActivity(intent)
                    }
                    binding.locationText.visibility = View.GONE
                    binding.locationLayout.visibility = View.GONE
                    binding.eventtextMode.visibility = View.VISIBLE
                    binding.eventModeTextView.visibility = View.VISIBLE
                } else {
                    val location = event.location
                    val latitudeStr = event.latitude
                    val longitudeStr = event.longitude

                    if (!latitudeStr.isNullOrEmpty() && !longitudeStr.isNullOrEmpty()) {
                        try {
                            val latitude = latitudeStr.toDouble()
                            val longitude = longitudeStr.toDouble()
                            eventLatLng = LatLng(latitude, longitude)

                            // Only place marker if map is ready
                            googleMap?.let {
                                placeMarker(eventLatLng!!)
                            }

                            binding.locationLayout.visibility = View.VISIBLE
                            binding.locationText.visibility = View.VISIBLE
                            binding.eventtextMode.visibility = View.GONE
                            binding.eventModeTextView.visibility = View.GONE
                            binding.locationTextView.text = location ?: ""
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                            hideLocationViews()
                        }
                    } else {
                        hideLocationViews()
                    }
                }
            }
        }
    }

    private fun hideLocationViews() {
        binding.locationLayout.visibility = View.GONE
        binding.locationText.visibility = View.GONE
    }

    private fun fetchEventDetails() {
        val eventId = intent.getStringExtra("eventId").toString()
        launchIfInternetAvailable {
            viewmodel.hitGetEventdetailbyid(eventId)
        }
    }

    private fun calculateEndtime(startTimeStr: String, durationStr: String): String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return try {
            val startDate = format.parse(startTimeStr) ?: return ""
            val calendar = Calendar.getInstance().apply { time = startDate }

            val hrRegex = Regex("(\\d+)\\s*hr")
            val minRegex = Regex("(\\d+)\\s*min")

            val addHours = hrRegex.find(durationStr)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val addMinutes = minRegex.find(durationStr)?.groupValues?.get(1)?.toIntOrNull() ?: 0

            calendar.add(Calendar.HOUR_OF_DAY, addHours)
            calendar.add(Calendar.MINUTE, addMinutes)

            val endTimeStr = format.format(calendar.time)
            "$startTimeStr - $endTimeStr"
        } catch (e: Exception) {
            ""
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
                }
            }

            // If event coordinates were loaded before map ready, place marker now
            eventLatLng?.let {
                placeMarker(it)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
    }

    private fun placeMarker(latLng: LatLng) {
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_location)

        locationMarker?.remove()
        locationMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Event Location")
                .icon(icon)
        )

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }

    private fun setupRecyclerview(selectedImages: MutableList<String>) {
        imageAdapter = ImageAdapter(selectedImages.toMutableList()) { position ->
            val flag = intent.getBooleanExtra("flag", false)
            if (flag) {
                // Handle delete logic if needed
            }
            selectedImages.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }

        binding.imageRecylerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecylerview.adapter = imageAdapter
    }
}
