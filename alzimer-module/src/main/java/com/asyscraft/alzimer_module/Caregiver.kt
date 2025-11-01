package com.asyscraft.alzimer_module

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityCaregiverBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Caregiver : BaseActivity() {

    private lateinit var binding: ActivityCaregiverBinding
    private val viewModel: YourViewModel by viewModels()


    private lateinit var profileImageView: CircleImageView
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null


    private lateinit var dialog: AlertDialog

    var isEdit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaregiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val caregiverId = intent.getStringExtra("CAREGIVER_ID")



        binding.back.setOnClickListener{
            finish()
        }

        binding.edit.setOnClickListener{
            isEdit =! isEdit

            if(isEdit){
                binding.personalInfoCardEdit.visibility = View.VISIBLE
                binding.personalInfoCard.visibility = View.GONE
                binding.save.visibility = View.VISIBLE
                binding.edit.visibility = View.GONE
                binding.imgUpload.visibility = View.VISIBLE
            }else{
                binding.personalInfoCardEdit.visibility = View.GONE
                binding.personalInfoCard.visibility = View.VISIBLE
                binding.save.visibility = View.GONE
                binding.edit.visibility = View.VISIBLE
                binding.imgUpload.visibility = View.GONE
            }
        }


        profileImageView = binding.userdp
        binding.imgUpload.setOnClickListener{
            openGallery()
        }

        binding.save.setOnClickListener{

            val name = binding.nameedit.text.toString()
            val relation = binding.relationedit.text.toString()
            val phonenumber = binding.phonenoedit.text.toString()
            val address = binding.addressedit.text.toString()


            if(validateForm(name, phonenumber, relation, address)){


                if (caregiverId != null) {
                    viewModel.UpdateCaregiverDetails(
                        context = this,
                        id = caregiverId,
                        fullName = name,
                        phonenumber = phonenumber,
                        address = address,
                        relation = relation,
                        patientId = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID) ?: "",
                        fcmtoken = "6234864373", // or use dynamic token if available
                        imageUri = selectedImageUri
                    )
                }

            }
        }

        binding.deleteIcon.setOnClickListener{
            showSuccessDialog(caregiverId)
        }



        if (caregiverId != null) {
            viewModel.CaregiverDetails(caregiverId)
            observeResponsecaregiverdetails()





        }

        observeResponseUpdateCaregiverDetails()
        observeResponseDeletecaregiver()
    }





    private fun showSuccessDialog(caregiverId: String?) {
        if (caregiverId == null) return
        val dialogView = layoutInflater.inflate(R.layout.delete_dialogue, null)



        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialogTheme)
        builder.setView(dialogView)

//        // Find and set up the Continue button

        // Show the dialog
        dialog = builder.create()

        val continueButton = dialogView.findViewById<TextView>(R.id.delete)
        val cancel = dialogView.findViewById<TextView>(R.id.cancel)
        continueButton.setOnClickListener {
            // Navigate to the next activity
//            val intent = Intent(this, JobDescription::class.java)
//            startActivity(intent)
            // Optionally, finish the current activity
            viewModel.DeleteCaregiverdetails(caregiverId)


        }

        cancel.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun observeResponsecaregiverdetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.CaregiverDetails.observe(this@Caregiver) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Caregiver)
                            Log.d("CaregiverDetails", "🔄 Loading...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    binding.name.text = data.data.name
                                    binding.text.text = data.data.name
                                    binding.nameedit.setText(data.data.name)

                                    binding.relation.text = data.data.relation
                                    binding.relationedit.setText(data.data.relation)

                                    binding.phoneno.text = data.data.phoneNumber
                                    binding.phonenoedit.setText(data.data.phoneNumber)

                                    binding.address.text = data.data.address
                                    binding.addressedit.setText(data.data.address)

                                    val baseUrl = "http://172.104.206.4:5000/uploads/"
                                    val fullImageUrl = baseUrl + data.data.image

                                    Picasso.get()
                                        .load(fullImageUrl)
                                        .placeholder(R.drawable.user_dummy_image) // Optional: shows while loading
                                        .error(R.drawable.user_dummy_image)             // Optional: shows on error
                                        .into(binding.userdp)
                                } else {
                                    androidExtension.alertBox(data.msg, this@Caregiver)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("CaregiverDetails", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Caregiver)
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("CaregiverDetails", "⚠️ Unexpected state: $response")
                            androidExtension.alertBox("Unexpected error", this@Caregiver)
                        }
                    }
                }
            }
        }
    }




    private fun observeResponseDeletecaregiver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.DeleteCaregiver.observe(this@Caregiver) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Caregiver)
                            Log.d("DeleteCaregiver", "🔄 Deleting caregiver...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    dialog.dismiss()
                                    finish()
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Unable to delete caregiver", this@Caregiver)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("DeleteCaregiver", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Caregiver)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error", this@Caregiver)
                        }
                    }
                }
            }
        }
    }

    private fun observeResponseUpdateCaregiverDetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.UpdateCaregiverDetails.observe(this@Caregiver) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Caregiver)
                            Log.d("UpdateCaregiver", "🔄 Updating caregiver info...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    // Update the display views with the new data
                                    binding.name.text = binding.nameedit.text.toString()
                                    binding.relation.text = binding.relationedit.text.toString()
                                    binding.phoneno.text = binding.phonenoedit.text.toString()
                                    binding.address.text = binding.addressedit.text.toString()
                                    binding.text.text = binding.nameedit.text.toString()

                                    binding.personalInfoCardEdit.visibility = View.GONE
                                    binding.personalInfoCard.visibility = View.VISIBLE
                                    binding.save.visibility = View.GONE
                                    binding.edit.visibility = View.VISIBLE
                                    binding.imgUpload.visibility = View.GONE
                                } else {
                                    androidExtension.alertBox(data.msg, this@Caregiver)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("UpdateCaregiver", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Caregiver)
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("UpdateCaregiver", "⚠️ Unexpected state: $response")
                            androidExtension.alertBox("Unexpected error", this@Caregiver)
                        }
                    }
                }
            }
        }
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
}