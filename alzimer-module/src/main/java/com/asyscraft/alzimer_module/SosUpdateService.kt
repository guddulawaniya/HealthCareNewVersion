package com.asyscraft.alzimer_module

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.careavatar.core_model.alzimer.updatelocation_request
import com.careavatar.core_service.repository.UserRepository
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class SosUpdateService : Service(), CoroutineScope {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: String = ""
    private var longitude: String = ""

    @Inject
    lateinit var repository: UserRepository
    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForegroundServiceWithNotification()
        startRepeatingUpdate()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundServiceWithNotification() {
        val channelId = "sos_channel"
        val channelName = "SOS Updates"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("SOS Active")
            .setContentText("Sending location updates...")
//            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .build()

        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    private fun startRepeatingUpdate() {
        launch {
            while (true) {
                getLocationAndUpdate()
                delay(2000L) // every 2 seconds
            }
        }
    }

    private fun getLocationAndUpdate() {
        val request = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }


        if (
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    Log.d("SOS", "Received location update")
                    val location = result.lastLocation
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        val patientId = SavedPrefManager.getStringPreferences(applicationContext, SavedPrefManager.PATIENT_ID)
                        if (!patientId.isNullOrEmpty()) {
                            Log.d("SOS", "Sending update: $latitude, $longitude")

                            val request = updatelocation_request(patientId, longitude, latitude)

                            launch {
                                try {
                                    val response = repository.Updatelocation(request)
                                    Log.d("SOS", "Location updated successfully: $response")
                                } catch (e: Exception) {
                                    Log.e("SOS", "Failed to update location", e)
                                }
                            }

                        }
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}
