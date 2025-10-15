package com.asyscraft.login_module.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.asyscraft.login_module.databinding.ActivityLoginBinding
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.asyscraft.login_module.viewModel.LoginViewModel
import com.careavatar.core_service.repository.viewModels.OnboardingRepositoryUserHobbies
import com.careavatar.userapploginmodule.utils.LocationHelper
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var locationHelper: LocationHelper

    @Inject
    lateinit var repository: OnboardingRepositoryUserHobbies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize helper
        locationHelper = LocationHelper(this) { locationData ->
            if (locationData != null) {
                Log.d("Location", "📍 Address: ${locationData.address}")
                Log.d("Location", "Lat: ${locationData.latitude}, Lon: ${locationData.longitude}")
                repository.onboardingInfo.address = locationData.address


            } else {
                showToast("Failed to get location")
            }
        }

        setupCountryCodePicker()
        setupTextWatcher()
        setupListeners()

        val maxLength = setMaxLengthByCountry(binding.ccp.selectedCountryNameCode)
        binding.etUserMobileNumber.filters = arrayOf(InputFilter.LengthFilter(maxLength))

        val isValid = isValidPhoneNumber(
            binding.etUserMobileNumber.text.toString(),
            binding.ccp.selectedCountryNameCode
        )


        binding.buttonNextInclude.buttonNext.isEnabled = isValid
        binding.buttonNextInclude.buttonNext.alpha = if (isValid) 1.0f else 0.8f

        loginObserver()
        checkLocation()

    }

    private fun checkLocation() {
        if (locationHelper.hasPermission()) {
            locationHelper.checkGpsEnabled(gpsLauncher) {
                locationHelper.getCurrentLocation()
            }
        } else {
            locationHelper.requestPermission(permissionLauncher)
        }
    }

    // Permission launcher
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                locationHelper.checkGpsEnabled(gpsLauncher) {
                    locationHelper.getCurrentLocation()
                }
            } else {
                showToast("❌ Location permission denied")
            }
        }
    private val gpsLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                locationHelper.getCurrentLocation()
            } else {
                showToast("⚠️ GPS required to continue")
            }
        }

    private fun setupTextWatcher() {

        binding.etUserMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val mobileNumber = s.toString()
                val isValid = isValidPhoneNumber(mobileNumber, binding.ccp.selectedCountryNameCode)

                binding.buttonNextInclude.buttonNext.isEnabled = isValid
                binding.buttonNextInclude.buttonNext.alpha = if (isValid) 1.0f else 0.8f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }


    private fun setMaxLengthByCountry(countryCode: String): Int {
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val exampleNumber = phoneUtil.getExampleNumberForType(
                countryCode,
                PhoneNumberUtil.PhoneNumberType.MOBILE
            )
            exampleNumber?.let {
                phoneUtil.getNationalSignificantNumber(it).length
            } ?: 10
        } catch (e: Exception) {
            Log.e("PhoneNumber", "Failed to get max length for $countryCode", e)
            10 // Fallback default
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String?, countryCode: String): Boolean {
        if (phoneNumber.isNullOrBlank()) return false

        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val numberProto = phoneUtil.parse(phoneNumber.trim(), countryCode)
            phoneUtil.isValidNumber(numberProto)
        } catch (e: NumberParseException) {
            Log.e("Validation", "Error parsing phone number: $phoneNumber for $countryCode", e)
            false
        }
    }


    private fun loginObserver() {
        collectApiResultOnStarted(viewModel.loginResponse) {
            startActivity(
                Intent(this, VerifyOtpActivity::class.java)
                    .putExtra("otp", it.otp)
                    .putExtra("mobile", it.phoneNumber)
            )

        }
    }


    private fun setupCountryCodePicker() {

        binding.apply {
            ccp.setCountryForPhoneCode(91)
            ccp.showNameCode(false)
            ccp.showFullName(false)
            ccp.setOnCountryChangeListener {
                val maxLength = setMaxLengthByCountry(binding.ccp.selectedCountryNameCode)
                binding.etUserMobileNumber.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            }
        }

    }

    private fun setupListeners() {
        binding.buttonNextInclude.buttonNext.setOnClickListener {
            if (isValidateForm()) {
                val request =
                    LoginRequest(binding.ccp.selectedCountryCodeWithPlus + binding.etUserMobileNumber.text.toString())
                hitLogin(request)
            } else {
                binding.etUserMobileNumber.error = getString(R.string.please_enter_mobile_number)
            }
        }
    }

    private fun hitLogin(request: LoginRequest) {
        launchIfInternetAvailable {
            viewModel.hitLogin(request)
        }
    }

    private fun isValidateForm(): Boolean {
        val mobileNumber = binding.etUserMobileNumber.text.toString()
        return when {
            mobileNumber.isEmpty() -> {
                showToast("Please enter mobile number")
                false
            }

            !isValidPhoneNumber(mobileNumber, binding.ccp.selectedCountryNameCode) -> {
                showToast("Invalid mobile number")
                false
            }

            else -> true
        }
    }

}