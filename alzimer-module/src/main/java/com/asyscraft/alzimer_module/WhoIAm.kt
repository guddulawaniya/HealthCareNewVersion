package com.asyscraft.alzimer_module

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.alzimer_module.adapter.AdapterCaregiverbyid
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.adapter.AdapterCaregiver
import com.careavatar.core_model.alzimer.CaregiverItem
import com.careavatar.core_model.alzimer.Caregiverres
import com.asyscraft.alzimer_module.databinding.ActivityWhoIamBinding
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
class WhoIAm : BaseActivity() {

    private lateinit var binding: ActivityWhoIamBinding
    private val viewModel: YourViewModel by viewModels()

    private lateinit var adapter: AdapterCaregiver
    private var datalist: ArrayList<Caregiverres> = ArrayList()

    private lateinit var adapterbyid: AdapterCaregiverbyid
    private var datalistbyid: ArrayList<CaregiverItem> = ArrayList()

    private lateinit var profileImageView: CircleImageView
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    private lateinit var type: String
    private lateinit var patientid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWhoIamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        Log.d("tyyyypee", type)





        binding.addCaregiver.setOnClickListener{
            val intent = Intent(this, AddCaregiver::class.java)
            startActivity(intent)
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.edit.setOnClickListener{

            binding.edit.visibility = View.GONE
            binding.save.visibility = View.VISIBLE
            binding.nameEdit.visibility = View.VISIBLE
            binding.personalInfoEdit.visibility = View.VISIBLE
            binding.name.visibility = View.GONE
            binding.personalInfo.visibility = View.GONE
            binding.imgUpload.visibility = View.VISIBLE


        }

        patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID).toString()


        Log.d("patieeeentid", patientid)


        binding.save.setOnClickListener{


            val name = binding.nameEdit.text.toString()
            val age = binding.ageedit.text.toString().toIntOrNull()
            val gender = binding.genderedit.text.toString()
            val phoneno = binding.phonenoedit.text.toString()



            if(age?.let { it1 -> validateForm(name, phoneno, it1, gender) } == true){
                Log.d("age", "click on button")

                if (patientid != null) {

                    Log.d("patientid", "click on button")


                    viewModel.UpdateAlzheimerDetails(
                        id = patientid,
                        fullName = name,
                        age = age,
                        gender = gender,       // Replace with actual longitude string
                        phoneNumber = phoneno,
                        imageUri = selectedImageUri, // This should be a valid Uri (e.g., from gallery/camera)
                        context = this  // Or `this@WhoIAm` if in an Activity
                    )
                }


            }
        }


        profileImageView = binding.userdp
        binding.imgUpload.setOnClickListener{
            openGallery()
        }

        if(type == "Caregiver"){
            setupRecyclerViewbyid()
            observeResponsegetallcaregiverbyid()
            if (patientid != null) {
                viewModel.GetAlzimerbyid(patientid)
                viewModel.GetCaregiverbyid(patientid)


            }
        }

        if(type == "Patient"){

            setupRecyclerView()
            observeResponsegetallcaregiver()
            viewModel.AlzimerDetails()
            viewModel.getallcaregiver()



        }



        observeAlzimerDetails()

        observeResponseUpdateAlzehimerDetails()
        observeResponsegetAlzheimerDetails()



    }


    override fun onResume() {
        super.onResume()

        if(type == "Patient"){
            viewModel.getallcaregiver()
        }

        if(type == "Caregiver"){
            if (patientid != null) {

                viewModel.GetCaregiverbyid(patientid)


            }
        }

    }



    private fun setupRecyclerView() {

        adapter = AdapterCaregiver(this, datalist) { game ->
            Toast.makeText(this, "Clicked on: ${game.name}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Caregiver::class.java)
            intent.putExtra("CAREGIVER_ID", game._id)
            startActivity(intent)
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerview.adapter = adapter



    }







    private fun setupRecyclerViewbyid() {

        adapterbyid = AdapterCaregiverbyid(this, datalistbyid) { game ->
            Toast.makeText(this, "Clicked on: ${game.name}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Caregiver::class.java)
            intent.putExtra("CAREGIVER_ID", game._id)
            startActivity(intent)
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerview.adapter = adapterbyid



    }



    private fun observeResponsegetallcaregiver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetAllCaregiver.observe(this@WhoIAm) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@WhoIAm)
                            Log.d("GetAllCaregiver", "🔄 Loading...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    datalist.clear()
                                    datalist.addAll(data.data)
                                    adapter.notifyDataSetChanged()

                                    // Update visibility based on data
                                    if (datalist.isEmpty()) {
                                        binding.recyclerview.visibility = View.GONE
                                        binding.img.visibility = View.VISIBLE
                                    } else {
                                        binding.recyclerview.visibility = View.VISIBLE
                                        binding.img.visibility = View.GONE
                                    }
                                } else {
                                    androidExtension.alertBox(data.msg, this@WhoIAm)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("GetAllCaregiver", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@WhoIAm)
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("GetAllCaregiver", "⚠️ Unexpected state: $response")
                            androidExtension.alertBox("Unexpected error", this@WhoIAm)
                        }
                    }
                }
            }
        }
    }



    private fun observeResponseUpdateAlzehimerDetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.UpdateAlzheimerDetails.observe(this@WhoIAm) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@WhoIAm)
                            Log.d("AlzheimerUpdate", "🔄 Updating Alzheimer details...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("alzehimerresponse", "✅ Update successful")

                                    // Update UI with new data
                                    binding.name.text = binding.nameEdit.text.toString()
                                    binding.Age.text = binding.ageedit.text.toString()
                                    binding.gender.text = binding.genderedit.text.toString()
                                    binding.contact.text = binding.phonenoedit.text.toString()

                                    // Show/hide views accordingly
                                    binding.save.visibility = View.GONE
                                    binding.edit.visibility = View.VISIBLE
                                    binding.nameEdit.visibility = View.GONE
                                    binding.personalInfoEdit.visibility = View.GONE
                                    binding.name.visibility = View.VISIBLE
                                    binding.personalInfo.visibility = View.VISIBLE
                                    binding.imgUpload.visibility = View.GONE
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Update failed", this@WhoIAm)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("AlzheimerUpdate", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@WhoIAm)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@WhoIAm)
                        }
                    }
                }
            }
        }
    }



    private fun observeResponsegetallcaregiverbyid() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getcaregiverdetails.observe(this@WhoIAm) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@WhoIAm)
                            Log.d("CAREGIVER_BY_ID", "⏳ Loading caregiver data...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { dataList ->
                                datalistbyid.clear()
                                datalistbyid.addAll(dataList.data)
                                adapterbyid.notifyDataSetChanged()

                                // Update visibility based on data
                                if (datalistbyid.isEmpty()) {
                                    binding.recyclerview.visibility = View.GONE
                                    binding.img.visibility = View.VISIBLE
                                } else {
                                    binding.recyclerview.visibility = View.VISIBLE
                                    binding.img.visibility = View.GONE
                                }
                            } ?: run {
                                androidExtension.alertBox("Caregiver data not found", this@WhoIAm)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("CAREGIVER_BY_ID", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Failed to load caregiver list",
                                this@WhoIAm
                            )
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@WhoIAm)
                        }
                    }
                }

            }
        }
    }


    private fun validateForm(
        name: String,
        phoneNumber: String,
        age: Int,
        Gender: String
    ): Boolean {
        var isValid = true

        // Reset previous errors first
        binding.nameerror.visibility = View.GONE
        binding.ageediterror.visibility = View.GONE
        binding.phonenoditerror.visibility = View.GONE
        binding.genderediterror.visibility = View.GONE

        if (name.isBlank()) {
            binding.nameerror.visibility = View.VISIBLE
            binding.nameerror.text = "Please enter your name."
            isValid = false
        }

        if (phoneNumber.isBlank()) {
            binding.phonenoditerror.visibility = View.VISIBLE
            binding.phonenoditerror.text = "Please enter your phoneno."
            isValid = false
        }

        if (age == null) {
            binding.ageediterror.visibility = View.VISIBLE
            binding.ageediterror.text = "Please enter your address."
            isValid = false
        }

        if (Gender.isBlank()) {
            binding.genderediterror.visibility = View.VISIBLE
            binding.genderediterror.text = "Please enter your relation."
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



    private fun observeAlzimerDetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.AlzimerDetails.observe(this@WhoIAm) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Progresss.start(this@WhoIAm)
                            Log.d("ALZIMER_DETAILS", "🔄 Loading...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            result.data?.let { response ->
                                if (response.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${response.success}")

                                    binding.name.text = response.data.fullName
                                    binding.nameEdit.setText(response.data.fullName)
                                    binding.Age.text = "${response.data.age} Years"
                                    binding.ageedit.setText(response.data.age.toString())
                                    binding.gender.text = response.data.gender
                                    binding.genderedit.setText(response.data.gender)
                                    binding.contact.text = response.data.phoneNumber
                                    binding.phonenoedit.setText(response.data.phoneNumber)

                                    SavedPrefManager.saveStringPreferences(
                                        this@WhoIAm,
                                        SavedPrefManager.PATIENT_ID,
                                        response.data._id
                                    )

                                    val baseUrl = "http://172.104.206.4:5000/uploads/"
                                    val fullImageUrl = baseUrl + response.data.image

                                    Picasso.get()
                                        .load(fullImageUrl)
                                        .placeholder(R.drawable.user_dummy_image)
                                        .error(R.drawable.user_dummy_image)
                                        .into(binding.userdp)
                                } else {
                                    androidExtension.alertBox("Failed to load patient data", this@WhoIAm)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("ALZIMER_DETAILS", "❌ Error: ${result.message}")
                            androidExtension.alertBox(result.message ?: "Something went wrong", this@WhoIAm)
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("ALZIMER_DETAILS", "⚠️ Unexpected state: $result")
                            androidExtension.alertBox("Unexpected error", this@WhoIAm)
                        }
                    }
                }
            }
        }
    }


    private fun observeResponsegetAlzheimerDetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getalzimerdetails.observe(this@WhoIAm) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@WhoIAm)
                            Log.d("ALZHEIMER_DETAILS", "⏳ Loading details...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                Log.d("ALZHEIMER_DETAILS", "✅ Success")

                                binding.name.text = data.data.fullName
                                binding.nameEdit.setText(data.data.fullName)
                                binding.Age.text = "${data.data.age} Years"
                                binding.ageedit.setText(data.data.age.toString())
                                binding.gender.text = data.data.gender
                                binding.genderedit.setText(data.data.gender)
                                binding.contact.text = data.data.phoneNumber
                                binding.phonenoedit.setText(data.data.phoneNumber)

                                SavedPrefManager.saveStringPreferences(
                                    this@WhoIAm,
                                    SavedPrefManager.PATIENT_ID,
                                    data.data._id
                                )

                                val baseUrl = "http://172.104.206.4:5000/uploads/"
                                val fullImageUrl = baseUrl + data.data.image

                                Picasso.get()
                                    .load(fullImageUrl)
                                    .placeholder(R.drawable.user_dummy_image)
                                    .error(R.drawable.user_dummy_image)
                                    .into(binding.userdp)
                            } ?: run {
                                androidExtension.alertBox("Patient data not found", this@WhoIAm)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("ALZHEIMER_DETAILS", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Failed to load patient details",
                                this@WhoIAm
                            )
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@WhoIAm)
                        }
                    }
                }

                viewModel.error.observe(this@WhoIAm) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@WhoIAm, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}