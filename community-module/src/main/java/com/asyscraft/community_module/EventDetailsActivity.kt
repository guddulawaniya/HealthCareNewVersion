package com.asyscraft.community_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ImageAdapter
import com.asyscraft.community_module.databinding.ActivityEventDetailsBinding
import com.bumptech.glide.Glide
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EventDetailsActivity : BaseActivity() , OnMapReadyCallback {
    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var imageAdapter: ImageAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var locationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDataFromIntent()
    }
    private fun setDataFromIntent() {
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val mainImage = intent.getParcelableExtra<Uri>("mainImage")
        val selectedImages = intent.getStringArrayListExtra("selectedImages") ?: arrayListOf()
        val eventMode = intent.getBooleanExtra("eventmode",false)
        val eventDate = intent.getStringExtra("eventdate")

        val meetingLink = intent.getStringExtra("meetingLinkField")
        val eventTime = intent.getStringExtra("timePickTextView")
        val eventDuration = intent.getStringExtra("eventTimeDuration")


        setupRecyclerview(selectedImages)
//        binding.itemImageView.setImageURI(mainImage)

        // ✅ Set Main Image
        Glide.with(this)
            .load(mainImage)
            .placeholder(R.drawable.logo)
            .into(binding.itemImageView)

        if(eventMode){
            binding.locationText.visibility = View.GONE
            binding.locationLayout.visibility = View.GONE
            binding.eventtextMode.visibility = View.VISIBLE
            binding.eventModeTextView.visibility = View.VISIBLE
        }else{
            val location = intent.getStringExtra("location")
            val latitude = intent.getStringExtra("latitude").toString()
            val longitude = intent.getStringExtra("longitude").toString()
            binding.locationTextView.text = location ?: ""
            val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
            placeMarker(latLng)
            binding.locationLayout.visibility = View.VISIBLE
            binding.locationText.visibility = View.VISIBLE
            binding.eventtextMode.visibility = View.GONE
            binding.eventModeTextView.visibility = View.GONE
        }

        binding.joinbtn.buttonNext.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url",meetingLink)
            startActivity(intent)

        }


        binding.toolbar.tvTitle.text ="Event Details"
        binding.titleText.text = title ?: ""
        binding.descriptionTextView.text = description ?: ""
//        binding.eventModeTextView.text = if(eventMode) "Online" else "Offline"
        binding.eventdate.text = eventDate ?: ""
        binding.eventDuration.text = calculateEndtime(eventTime ?: "",eventDuration ?: "")

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

            googleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    placeMarker(latLng)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
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

    private fun setupRecyclerview(selectedImages : ArrayList<String>) {
        imageAdapter = ImageAdapter(selectedImages, onClickItem = { position ->
            val flag = intent.getBooleanExtra("flag", false)
            val imageIdentifier = selectedImages[position]

            if (flag) {
//                deleteEventImage(imageIdentifier, position)
            }

            selectedImages.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        })
        binding.imageRecylerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecylerview.adapter = imageAdapter
    }
}