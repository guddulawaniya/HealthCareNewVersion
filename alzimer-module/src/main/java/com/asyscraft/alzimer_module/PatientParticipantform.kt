package com.asyscraft.alzimer_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityPatientParticipantformBinding
import com.careavatar.core_network.base.BaseActivity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientParticipantform : BaseActivity() {

    private lateinit var binding: ActivityPatientParticipantformBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: String? = null

    private var longitude: String? = null
    private lateinit var userType: String
    private val viewModeldashboard: YourViewModel by viewModels()

    private val viewModel: YourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPatientParticipantformBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLocationClient()
        initData()
        initListeners()
        observeViewModel()
    }

    /** ------------------------- INIT METHODS ------------------------- **/
    private fun initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

    private fun initData() {
        userType = intent.getStringExtra("type").orEmpty()
        viewModel.AlzimerDetails()
        viewModeldashboard.userDetails()
    }

    private fun initListeners() {
        binding.save.setOnClickListener { handleSaveClick() }
    }

    private fun observeViewModel() {
        observeFormResponse()
        observeAlzimerDetails()
        observeUserDetails()
    }

    /** ------------------------- UI ACTIONS ------------------------- **/
    private fun handleSaveClick() {
        val name = binding.name.text.toString().trim()
        val age = binding.age.text.toString().trim()
        val gender = binding.gender.text.toString().trim()
        val location = binding.location.text.toString().trim()
        val phone = binding.phoneno.text.toString().trim()

        if (latitude.isNullOrEmpty() || longitude.isNullOrEmpty()) {
            Toast.makeText(this, "Fetching location... Please allow location access.", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
            return
        }

        if (!validateForm(name, age, location, phone)) return

        saveLocationToPrefs(location)

        val fcmToken = SavedPrefManager.getStringPreferences(this, SavedPrefManager.FCMTOKEN) ?: "12334445"

        viewModel.FormData(
            fullName = name,
            age = age.toInt(),
            gender = gender,
            location = location,
            type = userType,
            phoneNumber = phone,
            latitude = latitude!!,
            longitude = longitude!!,
            fcmtoken = fcmToken
        )

        Log.d("ParticipantInformation", "User Type: $userType")
    }

    /** ------------------------- FORM VALIDATION ------------------------- **/
    private fun validateForm(name: String, ageStr: String, location: String, phone: String): Boolean {
        var isValid = true

        with(binding) {
            listOf(nameerror, ageError, genderError, locationError, phonenoerror).forEach { it.visibility = View.GONE }

            if (name.isBlank()) {
                nameerror.apply {
                    visibility = View.VISIBLE
                    text = "Please enter your name."
                }
                isValid = false
            }

            val age = ageStr.toIntOrNull()
            if (age == null || age <= 0 || age > 120) {
                ageError.apply {
                    visibility = View.VISIBLE
                    text = "Please enter a valid age."
                }
                isValid = false
            }

            if (!phone.matches(Regex("^\\+?\\d{8,15}$"))) {
                phonenoerror.apply {
                    visibility = View.VISIBLE
                    text = "Please enter a valid phone number."
                }
                isValid = false
            }

            if (location.isBlank()) {
                locationError.apply {
                    visibility = View.VISIBLE
                    text = "Please enter a location."
                }
                isValid = false
            }
        }
        return isValid
    }

    private fun saveLocationToPrefs(location: String) {
        getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .edit()
            .putString("selected_location", location)
            .apply()
    }

    /** ------------------------- LOCATION ------------------------- **/
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                } else {
                    requestNewLocation()
                }
            }
        }
    }

    private fun requestNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    latitude = it.latitude.toString()
                    longitude = it.longitude.toString()
                }
            }
        }, Looper.getMainLooper())
    }

    /** ------------------------- OBSERVERS ------------------------- **/
    private fun observeFormResponse() {
        viewModel.formResponse.observe(this) { response ->
            when {
                response.success -> {
                    navigateToBrainCheck()
                }
                response.isRegister && response.isAssessmentComplete -> {
                    navigateToHome()
                }
                response.isRegister -> {
                    navigateToBrainCheck()
                }
                else -> {
                    androidExtension.alertBox("Form submission failed", this)
                }
            }
        }
    }

    private fun observeUserDetails() {

        collectApiResultOnStarted(viewModeldashboard.userDetailsResponse) { result ->
            if (result.success) {
                result.user?.let { user ->
                    binding.apply {
                        name.setText(user.name)
                        phoneno.setText(user.phoneNumber.drop(3))
//              age.text = user.dob ?: ""
                        gender.text = user.gender.lowercase()
                    }
                }
            }
        }


    }

    private fun observeAlzimerDetails() {
        viewModel.AlzimerDetails.observe(this) { response ->
            when (response) {
                is Resource.Loading -> Progresss.start(this)
                is Resource.Success -> {
                    Progresss.stop()
                    response.data?.takeIf { it.success }?.let { detailResponse ->
                        SavedPrefManager.saveStringPreferences(
                            this,
                            SavedPrefManager.PATIENT_ID,
                            detailResponse.data._id
                        )
                    } ?: androidExtension.alertBox("Failed to load patient details", this)
                }
                is Resource.Error -> {
                    Progresss.stop()
                    Log.e("ALZIMER_DETAILS", "❌ Error: ${response.message}")
                }
                else -> Progresss.stop()
            }
        }
    }


    /** ------------------------- NAVIGATION ------------------------- **/
    private fun navigateToBrainCheck() {
        startActivity(Intent(this, BrainHealthCheck::class.java).apply {
            putExtra("type", userType)
        })
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomePage::class.java).apply {
            putExtra("type", userType)
        })
    }
}
