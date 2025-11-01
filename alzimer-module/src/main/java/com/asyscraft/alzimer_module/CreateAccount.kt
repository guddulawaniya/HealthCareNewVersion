package com.asyscraft.alzimer_module

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityCreateAccountBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CreateAccount : BaseActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    private val viewModel: YourViewModel by viewModels()

    private var selectedImageFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)





        binding.imageupload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.signupBtn.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val dob = binding.etDob.text.toString()
            val relativecontact = binding.etrelativescontactnumber.text.toString()
            val height = binding.etheight.text.toString()
            val weight = binding.etweight.text.toString()
            val startDate = "08-03-2025"
            val startTime = "06:10"
            val endTime = "18:30"
            val timeGap = "20"

            val token = SavedPrefManager.getStringPreferences(this, SavedPrefManager.TOKEN) ?: ""
            val phoneno = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PHONENO) ?: ""

            Log.d("phoneno", phoneno)

            selectedImageFile?.let { file ->
                viewModel.Createaccount(
                    token = "Bearer $token",
                    image = file,
                    name = name,
                    phoneNumber = phoneno,
                    email = email,
                    dob = dob,
                    latitude = "28.675663",
                    longitude = "77.312488",
                    gender = "male",
                    height = height,
                    weight = weight,
                    relativecontact = relativecontact,
                    startDate = startDate,
                    startTime = startTime,
                    endTime = endTime,
                    timeGap = timeGap
                )
            }
        }



        observeResponseCreateAccount()

    }


//    private fun observeResponseCreateAccount() {
//        viewModel.createaccount.observe(this@CreateAccount) { response ->
//            Log.d("LiveData", "Observer triggered: $response")
//            if (response.success) {
//                Toast.makeText(this@CreateAccount, "Successfully created", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this@CreateAccount, MainActivity::class.java)
//                startActivity(intent)
//                finish() // Prevent returning to signup screen
//            } else {
//                Toast.makeText(this@CreateAccount, "Account creation failed: ${response.msg}", Toast.LENGTH_SHORT).show()
//                Log.e("LiveData", "response.success = false")
//            }
//        }
//    }




    private fun observeResponseCreateAccount() {
        viewModel.createaccount.observe(this@CreateAccount) { response ->
            Log.d("CreateAccountObserver", "Observer triggered: $response")

            when (response) {
                is Resource.Loading -> {
                    Progresss.start(this@CreateAccount)
                    Log.d("CreateAccountObserver", "⏳ Creating account...")
                }

                is Resource.Success -> {
                    Progresss.stop()
                    val data = response.data
                    if (data?.success == true) {
                        Toast.makeText(this@CreateAccount, "Successfully created", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CreateAccount, AlzimerMainActivity::class.java)
                        startActivity(intent)
                        finish() // Prevent returning to signup screen
                    } else {
                        androidExtension.alertBox(data?.msg ?: "Account creation failed", this@CreateAccount)
                    }
                }

                is Resource.Error -> {
                    Progresss.stop()
                    Log.e("CreateAccountObserver", "❌ Error: ${response.message}")
                    androidExtension.alertBox(response.message ?: "Something went wrong", this@CreateAccount)
                }

                else -> {
                    Progresss.stop()
                    androidExtension.alertBox("Unexpected error occurred", this@CreateAccount)
                }
            }
        }
    }




    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                val file = uriToFile(it)
                selectedImageFile = file
                binding.imageupload.setImageURI(it) // Optional: display image
            }
        }
    }


    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("selected_image", ".jpg", cacheDir)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}