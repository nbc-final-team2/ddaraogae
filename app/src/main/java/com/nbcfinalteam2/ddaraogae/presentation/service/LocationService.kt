package com.nbcfinalteam2.ddaraogae.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity
import com.nbcfinalteam2.ddaraogae.presentation.util.DistanceCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationRequest by lazy {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (locationList.isNotEmpty()) {
                    serviceInfoState.update { prev ->
                        prev.copy(
                            distance = prev.distance + DistanceCalculator.getDistance(
                                locationList.last().latitude,
                                locationList.last().longitude,
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
                locationList.add(location.toLatLng())
            }
        }
    }

    private val binder = LocalBinder()

    val locationList = mutableListOf<LatLng>()
    var savedDogIdList: List<DogInfo>? = null
    var savedStartDate: Date? = null
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.is_walking))
            .setSmallIcon(R.drawable.ic_walk)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        startTimer()
        startLocationUpdates()
        isRunning = true
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    fun saveData(dogIdList: List<DogInfo>, startDateTime: Date) {
        if(savedDogIdList == null && savedStartDate == null) {
            savedDogIdList = dogIdList
            savedStartDate = startDateTime
        }
    }

    fun stopService() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        locationList.clear()
        savedDogIdList = null
        savedStartDate = null
        distanceSumState.update { _ ->
            0.0
        }
        stopTimer()
        serviceInfoState.update { _ ->
            ServiceInfoState(
                0.0,
                0
            )
        }
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun startLocationUpdates() {
        if(isRunning) return
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    private fun startTimer() {
        if(isRunning) return
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000)
                serviceInfoState.update { prev ->
                    prev.copy(
                        time = prev.time + 1
                    )
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        serviceInfoState.update { prev ->
            prev.copy(
                time = 0
            )
        }
    }

    data class LatLng(
        val latitude: Double,
        val longitude: Double
    )

    private fun Location.toLatLng() = LatLng(latitude = latitude, longitude = longitude)

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    companion object {
        private const val CHANNEL_ID = "LocationServiceChannel"
        private const val NOTIFICATION_ID = 1

        val distanceSumState = MutableStateFlow(0.0)
        val serviceInfoState = MutableStateFlow(ServiceInfoState.init())

        var isRunning = false
    }
}