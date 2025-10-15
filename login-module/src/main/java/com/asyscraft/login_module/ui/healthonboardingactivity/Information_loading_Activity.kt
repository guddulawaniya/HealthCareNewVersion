package com.asyscraft.login_module.ui.healthonboardingactivity

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.asyscraft.login_module.databinding.ActivityInformationLoadingBinding
import com.asyscraft.login_module.viewModel.LoginViewModel
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_service.repository.viewModels.OnboardingRepository
import com.careavatar.core_service.repository.viewModels.OnboardingRepositoryUserHobbies
import com.careavatar.core_ui.R
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class Information_loading_Activity : BaseActivity() {
    private lateinit var binding: ActivityInformationLoadingBinding

    @Inject
    lateinit var repository: OnboardingRepository

    @Inject
    lateinit var repositoryUserHobbies: OnboardingRepositoryUserHobbies

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PrintData()
        hitRegister()
        observer()

        // Collect all progress Views
        val progressViews = mutableListOf<View>()

        for (i in 0 until binding.progressContainer.childCount) {
            progressViews.add(binding.progressContainer.getChildAt(i))
            // Reset color to borderColor
            progressViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.disableBtnColor))
        }

        // Total animation duration in ms
        val totalDuration = 5000L
        val steps = progressViews.size
        val stepDuration = totalDuration / steps

        // Animate sequentially using coroutine
        lifecycleScope.launch {
            for (i in progressViews.indices) {
                val colorFrom =
                    (progressViews[i].background as? ColorDrawable)?.color
                        ?: ContextCompat.getColor(
                            this@Information_loading_Activity,
                            R.color.disableBtnColor
                        )
                val colorTo =
                    ContextCompat.getColor(this@Information_loading_Activity, R.color.primaryColor)

                val animator = ValueAnimator.ofArgb(colorFrom, colorTo)
                animator.duration = stepDuration
                animator.addUpdateListener { valueAnimator ->
                    progressViews[i].setBackgroundColor(valueAnimator.animatedValue as Int)
                }
                animator.start()

                // Wait for this step duration before animating next
                delay(stepDuration)
            }

            // After all animations finished, go to next screen
            startActivity(Intent(this@Information_loading_Activity, CommitActivity::class.java))
            finish()
        }
    }

    private  fun PrintData(){
        val data = repository.onboardingData
        val data1 = repositoryUserHobbies.onboardingInfo

        Log.d("Name",data.name.toString())
        Log.d("Age",data.age.toString())
        Log.d("Gender",data.gender.toString())
        Log.d("EmergencyContacts",data.emergencyContacts.toString())
        Log.d("Height",data.height.toString())
        Log.d("Weight",data.weight.toString())
        Log.d("Email",data.email.toString())
        Log.d("BloodPressure",data.bloodPressure.toString())
        Log.d("SugarLevel",data.sugarLevel.toString())
        Log.d("userPreferences",data1.userPreferences.toString())
        Log.d("addressData",data1.address.toString())
        Log.d("ImageFile",data.imageFile.toString())
    }


    private fun observer() {
        collectApiResultOnStarted(loginViewModel.signUpResponse) {
            if (it.success) {
                val data = repositoryUserHobbies.onboardingInfo
                val request = UserHobbiessResquest(ArrayList(data.userPreferences), data.address.toString())
                loginViewModel.hitUserHobbies(request)

                lifecycleScope.launch {
                    userPref.setLoggedIn(true)
                    userPref.setUserId(it.user._id.toString())
                    userPref.setMobileNumber(it.user.phoneNumber.toString())

                }
            }
        }
    }


    private fun getImageFileForUpload(imageFile : File?): File {
        return if (imageFile != null) {
            imageFile
        } else {
            val defaultDrawable =
                ContextCompat.getDrawable(this, R.drawable.profile_1) as BitmapDrawable
            val defaultBitmap = defaultDrawable.bitmap

            val file = File(this.cacheDir, "default_image.jpg")
            file.outputStream().use { outputStream ->
                defaultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            file
        }
    }


    private fun hitRegister() {

        launchIfInternetAvailable {
            val number = userPref.mobileNumber.firstOrNull()
            Log.d("Number", "$number")
            val data = repository.onboardingData



            loginViewModel.hitUserRegister(
                phoneNumber = number?.toRequestBody(),
                name = data.name?.toRequestBody(),
                dob = data.age?.toRequestBody(),
                gender = data.gender?.toRequestBody(),
                emergencycontact = data.emergencyContacts?.toRequestBody(),
                height = data.height?.toRequestBody(),
                weight = data.weight?.toRequestBody(),
                email = data.email?.toRequestBody(),
                BP = data.bloodPressure?.toRequestBody(),
                sugar = data.sugarLevel?.toRequestBody(),
                image = getImageFileForUpload(data.imageFile)
            )

        }
    }


}
