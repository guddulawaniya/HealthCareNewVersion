package com.asyscraft.alzimer_module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityWhereIamBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class WhereIAm : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityWhereIamBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: YourViewModel by viewModels()
    private  var imagedp : String? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWhereIamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // ✅ Now valid


        viewModel.AlzimerDetails()

        binding.back1.setOnClickListener {
            finish()
        }

        observeAlzimerDetails()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
//            showUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)


                createCustomMarker(this@WhereIAm, Constants.IMAGE_BASEURL) { bitmap ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(userLatLng)
                            .title("Alzheimer Location")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    )
                }

                // Move camera to user location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17f))


                // Get address info using Geocoder
                val geocoder = Geocoder(this, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val fullAddress = address.getAddressLine(0)
                        val placeName = address.featureName

                        binding.imagename.text = placeName ?: "Unknown place"
                        binding.address.text = fullAddress ?: "Address not found"
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to get address", Toast.LENGTH_SHORT).show()
                }


                val lat = it.latitude
                val lng = it.longitude
                val zoom = 17
                val size = "600x400" // width x height
                val apiKey = "AIzaSyAxmRrwWLag7plK-SQFtQvQxlhRVwI09tY"

                val staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?" + "center=$lat,$lng&zoom=$zoom&size=$size&markers=color:red|$lat,$lng&key=$apiKey"

                Picasso.get()
                    .load(staticMapUrl)
                    .into(binding.locationimg, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            binding.locationimg.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            binding.locationimg.visibility = View.GONE
                        }
                    })


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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun observeAlzimerDetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.AlzimerDetails.observe(this@WhereIAm) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Progresss.start(this@WhereIAm)
                            Log.d("ALZIMER_DETAILS", "🔄 Loading...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            result.data?.let { response ->
                                if (response.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${response.success}")


                                    imagedp = response.data.image

                                    showUserLocation()

                                } else {
                                    androidExtension.alertBox("Failed to load patient data", this@WhereIAm)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("ALZIMER_DETAILS", "❌ Error: ${result.message}")
                            androidExtension.alertBox(result.message ?: "Something went wrong", this@WhereIAm)
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("ALZIMER_DETAILS", "⚠️ Unexpected state: $result")
                            androidExtension.alertBox("Unexpected error", this@WhereIAm)
                        }
                    }
                }
            }
        }
    }
}
