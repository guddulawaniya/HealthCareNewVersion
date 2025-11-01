package com.asyscraft.alzimer_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityParticipantInformationBinding
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.careavatar.core_network.base.BaseActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParticipantInformation : BaseActivity() {

    private lateinit var binding: ActivityParticipantInformationBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var usertype: String


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: String? = null
    private var longitude: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usertype = intent.getStringExtra("type").toString() //Default to Patient

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()


        val months = mutableListOf("Select gender") + arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.gender.adapter = genderAdapter

        // Handle Spinner Selections
        binding.gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }


        binding.login.setOnClickListener {
            val intent = Intent(this, PatientList::class.java)
            intent.putExtra("type", usertype) // Pass Caregiver
            startActivity(intent)

        }



        binding.save.setOnClickListener {
            val name = binding.name.text.toString()
            val age = binding.age.text.toString().trim()
            val gender = binding.gender.selectedItem.toString().trim()
            val location = binding.location.text.toString()
            val phoneno = binding.phoneno.text.toString()
//            usertype = intent.getStringExtra("type")?:"Patient" //Default to Patient


            // 🔐 Check if location is available
            if (latitude.isNullOrEmpty() || longitude.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    "Fetching location... Please allow location access.",
                    Toast.LENGTH_SHORT
                ).show()
                getCurrentLocation() // Re-attempt to fetch location
                return@setOnClickListener
            }

            if (validateForm(name, age.toString(), gender, location, phoneno)) {

                val fcmtoken =
                    SavedPrefManager.getStringPreferences(this, SavedPrefManager.FCMTOKEN)
                        ?: "12334445"


                if (fcmtoken != null) {
                    viewModel.FormData(
                        fullName = name,
                        age = age.toInt(),
                        gender = gender.lowercase(),
                        location = location,
                        phoneNumber = phoneno,
                        latitude = latitude!!,
                        longitude = longitude!!,
//                        type = usertype,
                        type = "Patient",
                        fcmtoken = fcmtoken
                    )
                }

                Log.d("ParticipantInformation", "User Type: $usertype")
            }
        }

        viewModel.formResponse.observe(this) { response ->
            val origin = intent.getStringExtra("type")
            if (response.success) {
                Log.d("ALZHEIMER_SUCCESS", response.msg)


                // Navigate user to next screen or show success
                val intent = Intent(this, CaregiverForm::class.java)
                intent.putExtra("type", origin) // Pass Caregiver
                intent.putExtra("patientid", response.alzheimer?._id)
                startActivity(intent)
                finish()
            } else {

                if (response.isRegister) {

                    Toast.makeText(this, "Patient already exist", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, PatientList::class.java)
                    intent.putExtra("type", origin) // Pass Caregiver
                    val tr = intent.putExtra("isassesmentcomplete", response.isAssessmentComplete)
                    Log.d("compleeeet", tr.toString())
                    startActivity(intent)
                }
            }
        }

    }


    private fun validateForm(
        name: String,
        ageStr: String,
        gender: String,
        location: String,
        phoneno: String
    ): Boolean {
        var isValid = true

        // Reset previous errors first
        binding.nameerror.visibility = View.GONE
        binding.ageError.visibility = View.GONE
        binding.genderError.visibility = View.GONE
        binding.locationError.visibility = View.GONE
        binding.phoneError.visibility = View.GONE

        if (name.isBlank()) {
            binding.nameerror.visibility = View.VISIBLE
            binding.nameerror.text = "Please enter your name."
            isValid = false
        }

        val age = ageStr.toIntOrNull()
        if (age == null || age <= 0 || age > 120) {
            binding.ageError.visibility = View.VISIBLE
            binding.ageError.text = "Please enter your age."
            isValid = false
        }

        if (gender.isBlank()) {
            binding.genderError.visibility = View.VISIBLE
            binding.genderError.text = "Please enter your gender."
            isValid = false
        } else {
            val allowedGenders = listOf("Male", "Female", "Other")
            if (gender.replaceFirstChar { it.uppercase() } !in allowedGenders) {
                binding.genderError.visibility = View.VISIBLE
                binding.genderError.text = "Please select a valid gender (Male/Female)."
                isValid = false
            }
        }

        if (location.isBlank()) {
            binding.locationError.visibility = View.VISIBLE
            binding.locationError.text = "Please enter a location."
            isValid = false
        }

        if (phoneno.isBlank()) {
            binding.phoneError.visibility = View.VISIBLE
            binding.phoneError.text = "Please enter a phone no."
            isValid = false
        }

        return isValid
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    // Use latitude and longitude as needed
                } else {
                    // If last location is null, request new location
                    requestNewLocation()
                }
            }
        }
    }


    private fun requestNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        // Use latitude and longitude as needed
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

}