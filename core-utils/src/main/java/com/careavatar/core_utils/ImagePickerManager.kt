package com.careavatar.core_utils

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.careavatar.core_ui.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImagePickerManager {

    private var cameraLauncher: ActivityResultLauncher<Uri>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    private var cameraPermissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var storagePermissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var imageUri: Uri? = null
    private var callback: ((Uri?) -> Unit)? = null
    private var appContext: Context? = null  // store context for FileProvider

    fun init(activityResultCaller: ActivityResultCaller, context: Context) {
        appContext = context.applicationContext

        cameraPermissionLauncher =
            activityResultCaller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.all { it.value }) {
                    launchCamera()
                } else {
                    showToast("Camera permission denied")
                }
            }

        storagePermissionLauncher =
            activityResultCaller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.all { it.value }) {
                    launchGallery()
                } else {
                    showToast("Storage permission denied")
                }
            }

        cameraLauncher =
            activityResultCaller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    callback?.invoke(imageUri)
                } else {
                    callback?.invoke(null)
                }
            }

        galleryLauncher =
            activityResultCaller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                callback?.invoke(uri)
            }
    }

    fun showImageSourceDialog(context: Context, resultCallback: (Uri?) -> Unit) {
        callback = resultCallback

        val dialog =
            BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_image_source, null)
        dialog.setContentView(dialogView)

        // Transparent background to show rounded corners properly
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCamera = dialogView.findViewById<LinearLayout>(R.id.btnCamera)
        val btnGallery = dialogView.findViewById<LinearLayout>(R.id.btnGallery)
        val crossbtn = dialogView.findViewById<ImageView>(R.id.crossbtn)

        btnCamera.setOnClickListener {
            dialog.dismiss()
            requestCameraPermission()
        }
        crossbtn.setOnClickListener {
            dialog.dismiss()
        }

        btnGallery.setOnClickListener {
            dialog.dismiss()
            requestStoragePermission()
        }

        dialog.show()
    }



    private fun requestCameraPermission() {
        cameraPermissionLauncher?.launch(arrayOf(Manifest.permission.CAMERA))
    }

    private fun requestStoragePermission() {
        val permissions = mutableListOf<String>()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13+
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                permissions.add(Manifest.permission.READ_MEDIA_VIDEO) // if you need video
            }
            else -> {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        storagePermissionLauncher?.launch(permissions.toTypedArray())
    }

    private fun launchCamera() {
        imageUri = createImageUri()
        imageUri?.let {
            cameraLauncher?.launch(it)
        }
    }

    private fun launchGallery() {
        galleryLauncher?.launch("image/*")
    }


    private fun createImageUri(): Uri? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(
                appContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "IMG_$timestamp.jpg"
            )
            FileProvider.getUriForFile(
                appContext!!, // must be activity context
                "${appContext!!.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun showToast(message: String) {
        appContext?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}




