package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.addcaregiver_response
import com.asyscraft.alzimer_module.databinding.ActivityCaregiverFormBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaregiverForm : BaseActivity() {

    private lateinit var binding: ActivityCaregiverFormBinding
    private val viewModel: YourViewModel by viewModels()
    private val viewModelDashboard: YourViewModel by viewModels()

    private lateinit var userType: String
    private lateinit var patientId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaregiverFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initListeners()
        observeViewModel()
    }

    /** ------------------------- INIT ------------------------- **/
    private fun initData() {
        userType = intent.getStringExtra("type").orEmpty()
        patientId = intent.getStringExtra("patientid").orEmpty()
        viewModelDashboard.userDetails()
    }

    private fun initListeners() {
        binding.save.setOnClickListener { handleSaveClick() }
    }

    private fun observeViewModel() {
        observeUserDetails()
        observeAddCaregiverResponse()
    }

    /** ------------------------- UI ACTION ------------------------- **/
    private fun handleSaveClick() {
        val name = binding.name.text.toString().trim()
        val relation = binding.age.text.toString().trim()
        val location = binding.location.text.toString().trim()
        val phone = binding.phoneno.text.toString().trim()

        if (!validateForm(name, relation, location, phone)) return

        val fcmToken = SavedPrefManager.getStringPreferences(this, SavedPrefManager.FCMTOKEN) ?: "12334445"

        viewModel.Addcaregiver(
            fullName = name,
            phonenumber = phone,
            address = location,
            relation = relation,
            patientid = patientId,
            fcmtoken = fcmToken,
            imageUri = null,
            context = this
        )
    }

    /** ------------------------- VALIDATION ------------------------- **/
    private fun validateForm(name: String, relation: String, location: String, phone: String): Boolean {
        var isValid = true

        with(binding) {
            listOf(nameerror, ageError, locationError, phonenoerror).forEach { it.visibility = View.GONE }

            if (name.isBlank()) {
                nameerror.showError("Please enter your name.")
                isValid = false
            }
            if (relation.isBlank()) {
                ageError.showError("Please enter your relation.")
                isValid = false
            }
            if (location.isBlank()) {
                locationError.showError("Please enter a location.")
                isValid = false
            }
            if (phone.isBlank()) {
                phonenoerror.showError("Please enter a phone number.")
                isValid = false
            }
        }
        return isValid
    }

    private fun View.showError(message: String) {
        visibility = View.VISIBLE
        if (this is android.widget.TextView) text = message
    }


    /** ------------------------- OBSERVERS ------------------------- **/

    private fun observeUserDetails() {

        collectApiResultOnStarted(viewModelDashboard.userDetailsResponse){
            if (it.success){
                it.user.apply {
                    binding.apply {
                        name.setText(it.user.name)
                        phoneno.setText(it.user.phoneNumber.drop(3))
                    }
                }

            }
        }

    }


    private fun observeAddCaregiverResponse() {
        viewModel.addcaregiver.observe(this) { response ->
            val origin = intent.getStringExtra("type")
            when (response) {
                is Resource.Loading -> Progresss.start(this)
                is Resource.Success -> {
                    Progresss.stop()
                    response.data?.let { data ->
                        if (data.success) {
                            Log.d("CAREGIVER_SUCCESS", data.msg)
                            SavedPrefManager.saveStringPreferences(
                                this,
                                SavedPrefManager.CAREGIVER_ID,
                                data.caregiverId
                            )
                            navigateTo(PatientList::class.java, origin)
                        } else {
                            handleFailedResponse(data, origin)
                        }
                    }
                }
                is Resource.Error -> {
                    Progresss.stop()
                    androidExtension.alertBox(response.message ?: "Something went wrong", this)
                }
                else -> Progresss.stop()
            }
        }
    }

    /** ------------------------- HELPERS ------------------------- **/
    private fun handleFailedResponse(data: addcaregiver_response, origin: String?) {
        when {
            data.isRegister && data.isAssessmentComplete -> navigateTo(HomePage::class.java, origin)
            data.isRegister -> navigateTo(BrainHealthCheck::class.java, origin)
        }
        androidExtension.alertBox(data.msg, this)
        Log.e("CAREGIVER_FAILURE", data.msg)
    }



    private fun navigateTo(destination: Class<*>, origin: String?) {
        startActivity(Intent(this, destination).apply {
            putExtra("type", origin)
        })
        finish()
    }
}
