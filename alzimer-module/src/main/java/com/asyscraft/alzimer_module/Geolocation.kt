package com.asyscraft.alzimer_module

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityGeolocationBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.asyscraft.alzimer_module.utils.dpToPx
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.careavatar.core_network.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class Geolocation : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityGeolocationBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userCircle: Circle? = null
    private var userLatLng: LatLng? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val REQUEST_PERMISSION_CODE = 1

    private val viewModel: YourViewModel by viewModels()
    private lateinit var editText: EditText
    private lateinit var voiceButton: ImageView
    private lateinit var speechRecognizer: SpeechRecognizer
    private  var longituteAlzimer: Double = 0.0
    private  var latitudeAlzimer: Double = 0.0

    private  var imagedp : String? = null

    private  var newRadius: Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeolocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID)
        if (patientid != null) {
            viewModel.GetAlzimerLocation(patientid)
        }

        observeResponseGetAlzimerlocation()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        binding.distance.text = "${binding.seekBar.progress} Km"

        binding.back1.setOnClickListener { finish() }

        editText = findViewById(R.id.search)
        voiceButton = findViewById(R.id.voice)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        voiceButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
            }
        }

        binding.search.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val locationName = binding.search.text.toString()
                if (locationName.isNotEmpty()) {
                    searchLocation(locationName)
                }
                true
            } else {
                false
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val savedRadius = SavedPrefManager.getDoubleValue(this@Geolocation, SavedPrefManager.RADIUS)
        Log.d("confirmedSavedRadius", savedRadius.toString())


        if (patientid != null) {
            viewModel.Getradiussafezone(patientid)
        }


        binding.bookBtn.setOnClickListener {
            userCircle?.radius?.let {
                if (patientid != null) {
                    viewModel.Updateradius(patientid , radius = it)
                }
            }
        }

        observeResponseUpdateRadius()
        observeResponsegetradius()

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val percent = progress.toFloat() / (seekBar?.max ?: 1)
                val totalWidth = seekBar?.width ?: 0
                val newWidth = (totalWidth - 20.dpToPx()) * percent
                binding.progressTrack.layoutParams.width = newWidth.toInt()
                binding.progressTrack.requestLayout()


                binding.distance.text = "$progress Km"
                newRadius = progress * 1000.0
                userCircle?.radius = newRadius

                userLatLng?.let { latLng ->
                    updateCameraToFitCircle(latLng, newRadius)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun showUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLadtLng = LatLng(it.latitude, it.longitude)
                Log.d("currentlocation", userLadtLng.toString())

                userLatLng = LatLng(latitudeAlzimer, longituteAlzimer)
                Log.d("apilocationset", userLatLng.toString())

                var imageurl = "http://172.104.206.4:5000/uploads/${imagedp}"

                Log.d("immageurl", imageurl)


                createCustomMarker(this@Geolocation, imageurl) { bitmap ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(userLatLng!!)
                            .title("Alzheimer Location")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    )
                }





                val radiusInMeters = binding.seekBar.progress * 1000.0

                userCircle = googleMap.addCircle(
                    CircleOptions()
                        .center(userLatLng!!)
                        .radius(radiusInMeters)
                        .strokeColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                        .fillColor(ContextCompat.getColor(this, R.color.B4F0D6))
                        .strokeWidth(4f)
                )

                updateCameraToFitCircle(userLatLng!!, radiusInMeters)
            }
        }
    }

    private fun updateCameraToFitCircle(center: LatLng, radiusMeters: Double) {
        val bounds = LatLngBounds.builder()

        val distanceDegree = radiusMeters / 111000f // Rough approximation (1 degree ~ 111km)

        bounds.include(LatLng(center.latitude + distanceDegree, center.longitude + distanceDegree))
        bounds.include(LatLng(center.latitude - distanceDegree, center.longitude - distanceDegree))

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
    }




    private fun observeResponseUpdateRadius() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Updateradius.observe(this@Geolocation) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Geolocation)
                            Log.d("RadiusUpdate", "🔄 Updating radius...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("RadiusUpdate", "✅ Radius updated successfully")

                                    val radiusToSave = userCircle?.radius ?: 0.0
                                    SavedPrefManager.saveDoubleValue(this@Geolocation, SavedPrefManager.RADIUS, radiusToSave)
                                    Log.d("udatedradius", radiusToSave.toString())

                                    finish()
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Radius update failed", this@Geolocation)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("RadiusUpdate", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Geolocation)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Geolocation)
                        }
                    }
                }
            }
        }
    }






    private fun observeResponsegetradius() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getradius.observe(this@Geolocation) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Geolocation)
                            Log.d("RadiusUpdate", "🔄 Updating radius...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("RadiusUpdate", "✅ Radius updated successfully")

                                    val radiusFromApi = data.data.radius // radius in meters
                                    val savedProgress = (radiusFromApi / 1000) // Convert meters to kilometers

                                    binding.seekBar.progress = savedProgress
                                    binding.distance.text = "$savedProgress Km"
                                    newRadius = radiusFromApi.toDouble()

                                    // Update the circle radius on map if needed
                                    userCircle?.radius = newRadius

                                    userLatLng?.let { latLng ->
                                        updateCameraToFitCircle(latLng, newRadius)
                                    }


                                } else {
//                                    androidExtension.alertBox(data.msg ?: "Radius update failed", this@Geolocation)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("RadiusUpdate", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Geolocation)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Geolocation)
                        }
                    }
                }
            }
        }
    }



    private fun observeResponseGetAlzimerlocation() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getalzimerlocation.observe(this@Geolocation) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Geolocation)
                            Log.d("GetAlzimerlocation", "🔄 Alzimer location..")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("GetAlzimerlocation", "✅ Alzimer location successfully")

                                    latitudeAlzimer = data.data.latitude
                                    longituteAlzimer = data.data.longitude

                                    Log.d("loactionobservationapi", "Latitude: $latitudeAlzimer, Longitude: $longituteAlzimer")

                                    imagedp = data.data.image



                                    showUserLocation()
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Alzimer location failed", this@Geolocation)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("GetAlzimerlocation", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Geolocation)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Geolocation)
                        }
                    }
                }
            }
        }
    }



    private fun createCustomMarker(
        context: Context,
        imageUrl: String,
        onBitmapReady: (Bitmap) -> Unit
    ) {
        val markerView = LayoutInflater.from(context).inflate(R.layout.image_layout, null)
        val markerImage = markerView.findViewById<CircleImageView>(R.id.marker_image)

        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    markerImage.setImageBitmap(resource)

                    markerView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

                    val bitmap = Bitmap.createBitmap(
                        markerView.measuredWidth,
                        markerView.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )

                    val canvas = Canvas(bitmap)
                    markerView.draw(canvas)

                    onBitmapReady(bitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(locationName, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)

                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng).title(locationName))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Geocoding failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now")

        try {
            startActivityForResult(intent, 100)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech recognition not supported on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.isMyLocationEnabled = true
//                        showUserLocation()
                    }
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startVoiceRecognition()
                } else {
                    Toast.makeText(this, "Permission denied to access microphone", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                editText.setText(result[0])
                searchLocation(result[0])
            }
        }
    }
}
