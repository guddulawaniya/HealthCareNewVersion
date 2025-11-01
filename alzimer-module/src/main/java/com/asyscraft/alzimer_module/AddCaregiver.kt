package com.asyscraft.alzimer_module


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityAddCaregiverBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCaregiver : BaseActivity() {

    private lateinit var binding: ActivityAddCaregiverBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var profileImageView: CircleImageView
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCaregiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.addCaregiver.setOnClickListener {
            val name = binding.name.text.toString()
            val relation = binding.relation.text.toString()
            val phonenumber = binding.phoneno.text.toString()
            val address = binding.address.text.toString()
            val patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID)

            if (validateForm(name, phonenumber, relation, address)) {
                val fcmtoken = SavedPrefManager.getStringPreferences(this, SavedPrefManager.FCMTOKEN) ?: "12334445"

                if (patientid != null) {
                    viewModel.Addcaregiver(
                        fullName = name,
                        phonenumber = phonenumber,
                        address = address,
                        relation = relation,
                        patientid = patientid,
                        fcmtoken = fcmtoken,
                        imageUri = selectedImageUri,
                        context = this
                    )
                }
            }
        }



        profileImageView = binding.userdp
        binding.imgUpload.setOnClickListener {
            openGallery()
        }



        observeAddCaregiverResponse()
    }


    private fun validateForm(
        name: String,
        phoneNumber: String,
        address: String,
        relation: String
    ): Boolean {
        var isValid = true

        // Reset previous errors first
        binding.nameerror.visibility = View.GONE
        binding.relationerror.visibility = View.GONE
        binding.phonenoerror.visibility = View.GONE
        binding.addresserror.visibility = View.GONE

        if (name.isBlank()) {
            binding.nameerror.visibility = View.VISIBLE
            binding.nameerror.text = "Please enter your name."
            isValid = false
        }

        if (phoneNumber.isBlank()) {
            binding.phonenoerror.visibility = View.VISIBLE
            binding.phonenoerror.text = "Please enter your phoneno."
            isValid = false
        }

        if (address.isBlank()) {
            binding.addresserror.visibility = View.VISIBLE
            binding.addresserror.text = "Please enter your address."
            isValid = false
        }

        if (relation.isBlank()) {
            binding.relationerror.visibility = View.VISIBLE
            binding.relationerror.text = "Please enter your relation."
            isValid = false
        }


        return isValid
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Add this function to get file name from Uri
    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                profileImageView.setImageURI(selectedImageUri)

                // Extract and show the image file name
                val fileName = getFileName(selectedImageUri!!)
//                binding.imageNameText.text = fileName ?: "Unknown File"
                Log.d("file name of image ", "${fileName}")


            }
        }
    }


    private fun observeAddCaregiverResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addcaregiver.observe(this@AddCaregiver) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@AddCaregiver)
                            Log.d("Caregiver_RESPONSE", "🔄 Loading...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("Caregiver_SUCCESS", data.msg)
                                    finish() // Navigate or finish current activity
                                } else {
                                    Log.e("Caregiver_FAILURE", data.msg)
                                    androidExtension.alertBox(data.msg, this@AddCaregiver)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("Caregiver_ERROR", response.message ?: "Unknown error")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                               this@AddCaregiver
                            )
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("Caregiver_UNKNOWN", "Unhandled state: $response")
                            androidExtension.alertBox("Unexpected error", this@AddCaregiver)
                        }
                    }
                }
            }
        }
    }


}