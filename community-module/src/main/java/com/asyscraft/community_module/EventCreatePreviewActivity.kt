package com.asyscraft.community_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ImageAdapter
import com.asyscraft.community_module.databinding.ActivityEventCreatePreviewBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.net.toUri
import com.google.android.gms.maps.SupportMapFragment

@AndroidEntryPoint
class EventCreatePreviewActivity : BaseActivity() ,OnMapReadyCallback{
    private lateinit var binding: ActivityEventCreatePreviewBinding
    private lateinit var imageAdapter: ImageAdapter
    private val viewModel : SocialMeetViewmodel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private var locationMarker: Marker? = null
    private lateinit var geocoder: Geocoder
    private var pendingLatLng: LatLng? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventCreatePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btninclude.buttonNext.text = "Create Event"

        // ✅ Initialize FusedLocationProviderClient
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager
            .findFragmentById(com.asyscraft.community_module.R.id.eventmapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btninclude.buttonNext.setOnClickListener {
            if (intent.getStringExtra("updateEvent")=="update"){
                launchIfInternetAvailable {
                    hitUpdateEvent()
                }
            }else{
                launchIfInternetAvailable {
                    hitCreateEvent()
                }
            }



        }
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
        setDataFromIntent()
        observer()

        if (intent.getStringExtra("updateEvent")=="update"){
            binding.btninclude.buttonNext.text = "Save Changes"
            observer()
        }

    }

    private fun observer(){
        collectApiResultOnStarted(viewModel.eventPostResponse){
            val intent = Intent(this, EventActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }



    private fun hitUpdateEvent() {

        val eventId = intent.getStringExtra("updateEvent").toString()
        val communityId = intent.getStringExtra("communityId").orEmpty()
        val title = intent.getStringExtra("title").orEmpty()
        val description = intent.getStringExtra("description").orEmpty()
        val eventMode = intent.getBooleanExtra("eventmode", false)
        val eventDate = intent.getStringExtra("selectedEventdate").orEmpty()
        val meetingLink = intent.getStringExtra("meetingLinkField").orEmpty()
        val eventTime = intent.getStringExtra("timePickTextView").orEmpty()
        val location = intent.getStringExtra("location").orEmpty()

        Log.d("PreviewEvent","latitude : "+ pendingLatLng?.latitude)
        Log.d("PreviewEvent","longitude"+pendingLatLng?.longitude)
        Log.d("PreviewEvent","address"+location.toString())


        val mode = if (eventMode) "online" else "offline"

        lifecycleScope.launch {

            viewModel.hitUpdateEvent(
                eventId = eventId,
                communityId = communityId,
                eventDate = eventDate,
                description = description,
                eventLink = meetingLink,
                eventMode = mode,
                eventTime = eventTime,
                latitude = pendingLatLng?.latitude.toString(),
                location = location,
                longitude = pendingLatLng?.longitude.toString(),
                title = title,
                visibility = "all",
            )
        }
    }
    private fun hitCreateEvent() {
        val communityId = intent.getStringExtra("communityId").orEmpty()
        val title = intent.getStringExtra("title").orEmpty()
        val description = intent.getStringExtra("description").orEmpty()
        val mainImageUri = intent.getStringExtra("mainImage")?.toUri()
        val selectedImages = intent.getStringArrayListExtra("selectedImages") ?: arrayListOf()

        val eventMode = intent.getBooleanExtra("eventmode", false)
        val eventDate = intent.getStringExtra("selectedEventdate").orEmpty()
        val meetingLink = intent.getStringExtra("meetingLinkField").orEmpty()
        val eventTime = intent.getStringExtra("timePickTextView").orEmpty()
        val latitude = intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude",0.0)
        val location = intent.getStringExtra("location").orEmpty()
        val eventDuration = intent.getStringExtra("eventTimeDuration").orEmpty()

        Log.d("PreviewEvent","latitude : "+ pendingLatLng?.latitude)
        Log.d("PreviewEvent","longitude"+pendingLatLng?.longitude)
        Log.d("PreviewEvent","address"+location.toString())


        val mode = if (eventMode) "online" else "offline"

        // Run everything inside a coroutine
        lifecycleScope.launch {
            val userId = userPref.userId.first().toString()
            val membersList = mutableListOf(userId)

            // 🔹 Combine main image + other selected images
            val allUris = mutableListOf<Uri>()
            mainImageUri?.let { allUris.add(it) }
            selectedImages.forEach { allUris.add(Uri.parse(it)) }

            val attachments = allUris.mapNotNull { uri ->
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) { /* ignore if not persisted */ }

                try {
                    val inputStream = contentResolver.openInputStream(uri) ?: return@mapNotNull null
                    val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
                    tempFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                    val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("attachments", tempFile.name, requestFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            // ✅ Call your API only after everything is ready
            viewModel.hitCreateEventData(
                communityId = communityId,
                eventDate = eventDate,
                description = description,
                eventLink = meetingLink,
                eventMode = mode,
                eventTime = eventTime,
                latitude = pendingLatLng?.latitude.toString(),
                location = location,
                longitude = pendingLatLng?.longitude.toString(),
                title = title,
                visibility = "all",
                notifiedMembers = membersList,
                attachments = attachments
            )
        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true

            // ✅ If we already have latitude/longitude from intent — show it immediately
            pendingLatLng?.let {
                Log.d("MapDebug", "Showing marker at pendingLatLng: $it")
                placeMarker(it)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            } ?: run {
                // fallback to current user location
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        Log.d("MapDebug", "Showing fallback marker at current location: $latLng")
                        placeMarker(latLng)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    }
                }
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }

        // Optional: allow user to tap map to move marker
        googleMap.setOnMapClickListener { latLng ->
            placeMarker(latLng)
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

    private fun setDataFromIntent() {
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val mainImage = intent.getStringExtra("mainImage")?.let { it.toUri() }
        val selectedImages = intent.getStringArrayListExtra("selectedImages") ?: arrayListOf()

        val eventMode = intent.getBooleanExtra("eventmode",false)
        val eventDate = intent.getStringExtra("eventdate")

        val meetingLink = intent.getStringExtra("meetingLinkField")
        val eventTime = intent.getStringExtra("timePickTextView")
        val eventDuration = intent.getStringExtra("eventTimeDuration")


        Log.d("mainimage",mainImage.toString())

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
        } else {
            val location = intent.getStringExtra("location")
            val latitudeStr = intent.getStringExtra("latitude")
            val longitudeStr = intent.getStringExtra("longitude")

            Log.d("PreviewEvent","latitude : "+ latitudeStr)
            Log.d("PreviewEvent","longitude : "+longitudeStr)
            Log.d("PreviewEvent","address"+location.toString())

            binding.locationTextView.text = location ?: ""

            if (!latitudeStr.isNullOrEmpty() && !longitudeStr.isNullOrEmpty()) {
                try {
                    pendingLatLng = LatLng(latitudeStr.toDouble(), longitudeStr.toDouble())
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    // Optionally show a toast or log
                    // showToast("Invalid location coordinates")
                }
            } else {
                // No valid coordinates — hide map section gracefully
                binding.locationLayout.visibility = View.GONE
                binding.locationText.visibility = View.GONE
            }

            binding.locationLayout.visibility = View.VISIBLE
            binding.locationText.visibility = View.VISIBLE
            binding.eventtextMode.visibility = View.GONE
            binding.eventModeTextView.visibility = View.GONE
        }



        binding.toolbar.tvTitle.text = title ?: ""
        binding.descriptionTextView.text = description ?: ""
        binding.eventModeTextView.text = if(eventMode) "Online" else "Offline"
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

}