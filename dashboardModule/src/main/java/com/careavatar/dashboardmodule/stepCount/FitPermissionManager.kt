package com.careavatar.dashboardmodule.stepCount

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

object FitPermissionManager {

    const val ACTIVITY_PERMISSION = 1001
    const val GOOGLE_FIT_PERMISSION = 2001

    val fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)      // for live updates & today
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ) // for historical steps
        .build()


    // Checks if account has the extension/permission
    fun hasGoogleFitPermission(context: Context): Boolean {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        return GoogleSignIn.hasPermissions(account, fitnessOptions)
    }

    // Silent sign in MUST include the fitness extension in the GSO
    fun silentSignIn(activity: Activity, onDone: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .addExtension(fitnessOptions) // <<< important
            .build()

        val client = GoogleSignIn.getClient(activity, gso)

        val last = GoogleSignIn.getLastSignedInAccount(activity)
        if (last != null && GoogleSignIn.hasPermissions(last, fitnessOptions)) {
            // already signed-in and has fitness permissions
            onDone()
            return
        }

        // attempt silent sign-in (may still return an account without fitness scope)
        client.silentSignIn().addOnCompleteListener { task ->
            // even if silentSignIn returns an account, it may not have fitness permission
            onDone()
        }
    }

    // Request runtime permission for activity recognition (Android 10+)
    fun requestActivityRecognitionPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    ACTIVITY_PERMISSION
                )
            }
        }
    }

    // Request Google Fit permission: use getAccountForExtension to ensure correct account/extension
    fun requestGoogleFitPermission(activity: Activity) {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
            ?: GoogleSignIn.getAccountForExtension(activity, fitnessOptions)

        GoogleSignIn.requestPermissions(
            activity,
            GOOGLE_FIT_PERMISSION,
            account,
            fitnessOptions
        )
    }
}
