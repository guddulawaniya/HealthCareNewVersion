package com.careavatar.core_utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionManager {


    fun getCameraPermission(): String = Manifest.permission.CAMERA

    fun getImagePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    }

    /** Get all image-related permissions depending on Android version */
    fun getAllImagePermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }

            else -> {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Request a single permission with rationale */
    fun requestPermission(
        fragment: Fragment,
        permission: String,
        launcher: ActivityResultLauncher<String>
    ) {
        when {
            isPermissionGranted(fragment.requireContext(), permission) -> {
                // Already granted, nothing to do
            }

            fragment.shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    fragment.requireContext(),
                    "This permission is needed for using this feature",
                    Toast.LENGTH_LONG
                ).show()
                launcher.launch(permission)
            }

            else -> launcher.launch(permission)
        }
    }

    /** Request multiple permissions safely */
    fun requestMultiplePermissions(
        fragment: Fragment,
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isEmpty()) {
            // All already granted
            return
        }

        // Check if we should show rationale
        val shouldShowRationale = deniedPermissions.any {
            fragment.shouldShowRequestPermissionRationale(it)
        }

        if (shouldShowRationale) {
            Toast.makeText(
                fragment.requireContext(),
                "We need these permissions to access camera and storage.",
                Toast.LENGTH_LONG
            ).show()
        }

        launcher.launch(deniedPermissions.toTypedArray())
    }
}
