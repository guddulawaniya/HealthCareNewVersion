package com.asyscraft.alzimer_module

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityImageTaskBinding

import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ImageTask : BaseActivity() {

    private lateinit var binding: ActivityImageTaskBinding

    private val imageUris = mutableMapOf<Int, Uri?>()
    private val IMAGE_PICK_REQUEST = 1001
    private var currentImageButtonId = 0


    private val viewModel: YourViewModel by viewModels()

    private lateinit var activityid: String
    private lateinit var title: String

    private lateinit var imageButtons: List<ImageButton>
    private lateinit var crossButtons: List<ImageButton>
    private lateinit var imageButtonToIndex: Map<Int, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        title = intent.getStringExtra("title").toString()

        Log.d("titlee", title)
//        title = "Gautam Test356762"
        activityid = intent.getStringExtra("activityid").toString()
//        activityid = "684c5d877839094e5cf8b91f"

        binding.headingText.setText(title)

        binding.back.setOnClickListener{
            finish()
        }



        imageButtons = listOf(
            binding.image1, binding.image2, binding.image3,
            binding.image4, binding.image5, binding.image6
        )


        crossButtons = listOf(
            binding.cross1, binding.cross2, binding.cross3,
            binding.cross4, binding.cross5, binding.cross6
        )

        // Map image button ID to index for easier cross button access
        imageButtonToIndex = imageButtons.mapIndexed { index, imageButton -> imageButton.id to index }.toMap()

//        val imageUris = mutableMapOf<Int, Uri?>() // ID -> Uri

        imageButtons.forEach { button ->
            button.setOnClickListener {
                currentImageButtonId = button.id
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, IMAGE_PICK_REQUEST)
            }
        }



        // Handle cross button clicks
        crossButtons.forEachIndexed { index, crossButton ->
            crossButton.setOnClickListener {
                val imageButton = imageButtons[index]
                imageButton.setImageResource(R.drawable.add_item_button)
                crossButton.visibility = View.GONE
                imageUris.remove(imageButton.id)
            }
        }



        binding.save.setOnClickListener {
            if (imageUris.isEmpty()) {
                Toast.makeText(this, "Please add at least one image", Toast.LENGTH_SHORT).show()
            } else {
                uploadImageFilesToApi() // ✅ merge only added images
            }
        }

        observeResponseactivityimage()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data ?: return
            imageUris[currentImageButtonId] = imageUri

            val imageButton = findViewById<ImageButton>(currentImageButtonId)
            imageButton.setImageURI(imageUri)


            // Show corresponding cross button
            val index = imageButtonToIndex[currentImageButtonId]
            if (index != null) {
                crossButtons[index].visibility = View.VISIBLE
            }

//            if (imageUris.size == 6 && !imageUris.values.contains(null)) {
//                mergeImagesAndUpload()
//            }
        }
    }





//    private fun uploadImageFileToApi(file: File) {
////        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
////        val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//        // Example Retrofit call
////        viewModel.uploadImage(multipart)
//
//        viewModel.ActivitySubmit(
//            title = title,
//            music = null,
//            thumbnail = file,
//            isCompleted = true,
//            activityId = activityid
//        )
//    }





//    private fun uploadImageFilesToApi() {
//        val parts = mutableListOf<MultipartBody.Part>()
//
//        imageUris.values.forEach { uri ->
//            uri?.let {
//                val file = uriToFile(it)
//                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//                val part = MultipartBody.Part.createFormData("thumbnail", file.name, requestFile)
//                parts.add(part)
//            }
//        }
//
//        if (parts.isEmpty()) {
//            Toast.makeText(this, "No images to upload", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        viewModel.ActivitySubmit(
//            title = title,
//            music = null,
//            thumbnail = parts,
//            isCompleted = true,
//            activityId = activityid
//        )
//    }





    private fun uploadImageFilesToApi() {
        val files = mutableListOf<File>()

        imageUris.values.forEach { uri ->
            if (uri != null) {
                val file = uriToFile(uri)
                files.add(file)
            }
        }

        if (files.isEmpty()) {
            Toast.makeText(this, "No images to upload", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.ActivitySubmit(
            title = title,
            music = null,
            thumbnail = files,  // ✅ Pass List<File>
            isCompleted = true,
            activityId = activityid
        )
    }





    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }



//    private fun observeResponseactivityimage() {
//        lifecycleScope.launch {
//
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.activitysubmit.observe(this@ImageTask) { response ->
//
//                    if(response.success){
//
//                        Toast.makeText(this@ImageTask, "Image send successfully:", Toast.LENGTH_SHORT).show()
//
//
//                        // ✅ Reset imageUris map
//                        imageUris.clear()
//
//                        // ✅ Reset ImageButtons to default image
//                        val defaultImage = R.drawable.add_item_button
//                        val imageButtons = listOf(
//                            binding.image1, binding.image2, binding.image3,
//                            binding.image4, binding.image5, binding.image6
//                        )
//                        imageButtons.forEach { it.setImageResource(defaultImage) }
//
//                        finish()
//
//                    }else{
//
//                    }
//
//                }
//            }
//        }
//    }




    private fun observeResponseactivityimage() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activitysubmit.observe(this@ImageTask) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@ImageTask)
                            Log.d("ImageUpload", "⏳ Uploading image...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            Toast.makeText(this@ImageTask, "✅ Image sent successfully", Toast.LENGTH_SHORT).show()

                            // Clear imageUris map
                            imageUris.clear()

                            // Reset image buttons
                            val defaultImage = R.drawable.add_item_button
                            val imageButtons = listOf(
                                binding.image1, binding.image2, binding.image3,
                                binding.image4, binding.image5, binding.image6
                            )
                            imageButtons.forEach { it.setImageResource(defaultImage) }

                            finish()
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("ImageUpload", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Failed to upload image", this@ImageTask)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@ImageTask)
                        }
                    }
                }


            }
        }
    }


}