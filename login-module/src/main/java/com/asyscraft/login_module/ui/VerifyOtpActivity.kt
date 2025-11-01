package com.asyscraft.login_module.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.asyscraft.login_module.databinding.ActivityVerifyOtpBinding
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_model.VerifyOtpRequest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.careavatar.dashboardmodule.DashboardActivity
import com.asyscraft.login_module.viewModel.LoginViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VerifyOtpActivity : BaseActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private var resendJob: Job? = null
    private val otpLength = 6
    private val resendTime = 30 // seconds
    private val viewModel: LoginViewModel by viewModels()
    private var devicetoken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        startResendTimer()
        setupOtpValidation()
        setupNextButton()
        setupResendClick()
        binding.buttonNext.buttonNext.isEnabled = binding.PinView.text?.length == otpLength
        setIntentData()
        verifyOtpObserver()
        getFcmToken()
        loginObserver()

    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the FCM token
            devicetoken = task.result
            Log.d("FCM", "FCM Token: $devicetoken")

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setIntentData() {
        val otp = intent.getStringExtra("otp").toString()
        val mobile = intent.getStringExtra("mobile").toString()

        binding.otpdammy.text = otp
        binding.tvUserMobileNumber.text =
            getString(R.string.enter_the_otp_sent_to_917467095730) + mobile
    }

    private fun setupOtpValidation() {
        binding.PinView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val otp = s.toString()

                // Enable Next only if OTP length is valid AND checkbox is checked
                binding.buttonNext.buttonNext.isEnabled = otp.length == otpLength && binding.checkbox.isChecked
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Enable button when checkbox is toggled
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            val otp = binding.PinView.text.toString()
            binding.buttonNext.buttonNext.isEnabled = otp.length == otpLength && isChecked
        }
    }

    private fun setupNextButton() {
        binding.buttonNext.buttonNext.setOnClickListener {
            val otp = binding.PinView.text.toString()

            if (otp.length == otpLength && binding.checkbox.isChecked) {

                hitVerifyOtp()

            } else {
                Toast.makeText(this, "Enter valid OTP and accept terms", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hitVerifyOtp() {

        val mobileNumber = intent.getStringExtra("mobile").orEmpty()

        val request = VerifyOtpRequest(
            mobileNumber.toString(),
            binding.PinView.text.toString(),
            devicetoken.toString()
        )

        launchIfInternetAvailable {
            viewModel.hitVerifyOtp(request)
        }
    }

    private fun loginObserver() {
        collectApiResultOnStarted(viewModel.loginResponse) {

            binding.otpdammy.text = it.otp

        }
    }


    private fun verifyOtpObserver() {
        collectApiResultOnStarted(viewModel.verifyOtpResponse) { response ->
            if (response.success) {
                lifecycleScope.launch {
                    // Save token and user info first
                    userPref.setCachedToken(response.token.toString())
                    userPref.setToken(response.token.toString())
                    userPref.setMobileNumber(intent.getStringExtra("mobile").orEmpty())

                    if (response.isFirstTime) {
                        // Navigate to PreferenceActivity if first time
                        val intent = Intent(this@VerifyOtpActivity, PreferenceActivity::class.java)
                        startActivity(intent)
                    } else {

                        userPref.setUserId(response.id.toString())
                        userPref.setLoggedIn(true)

                        // Navigate to DashboardActivity if not first time
                        val intent = Intent(this@VerifyOtpActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                    // Finish current activity
                    finish()
                }

            } else {
                showToast(response.msg.toString())
                binding.PinView.setLineColor(ContextCompat.getColor(this, R.color.red))
                binding.PinView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                binding.PinView.setText("")
            }
        }
    }


    private fun setupResendClick() {
        binding.resendTextview.setOnClickListener {
            if (binding.resendTextview.isEnabled) {
                sendOtp()
                startResendTimer()
            }
        }
    }


    private fun startResendTimer() {
        binding.resendTextview.isEnabled = false
        resendJob?.cancel()
        resendJob = lifecycleScope.launch {

            binding.resendTextview.setTextColor(
                ContextCompat.getColor(
                    this@VerifyOtpActivity,
                    R.color.grayTextColor
                )
            )

            var remaining = resendTime
            while (remaining > 0) {
                binding.resendTextview.text =
                    "Resend OTP in 00:${if (remaining < 10) "0$remaining" else remaining}"
                delay(1000)
                remaining--
            }
            binding.resendTextview.setTextColor(
                ContextCompat.getColor(
                    this@VerifyOtpActivity,
                    R.color.textColor
                )
            )
            binding.resendTextview.text = "Resend OTP"
            binding.resendTextview.isEnabled = true
        }
    }


    private fun sendOtp() {
        Toast.makeText(this, "OTP sent again", Toast.LENGTH_SHORT).show()
        binding.PinView.text?.clear()
        binding.buttonNext.buttonNext.isEnabled = false
        val mobile = intent.getStringExtra("mobile").toString()

        val request = LoginRequest(mobile)
        hitLogin(request)
    }

    private fun hitLogin(request: LoginRequest) {
        launchIfInternetAvailable {
            viewModel.hitLogin(request)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resendJob?.cancel()
    }
}
