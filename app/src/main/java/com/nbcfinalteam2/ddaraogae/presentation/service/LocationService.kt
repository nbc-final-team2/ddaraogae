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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.presentation.ui.main.MainActivity

class LocationService: Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationRequest by lazy {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for(location in locationResult.locations) {
                locationList.add(location.toLatLng())
                Log.d("onLocationResult()", locationList.toString())
            }
        }
    }

    private val binder = LocalBinder()

    val locationList = mutableListOf<LatLng>()

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
            .setContentTitle("따라오개")
            .setContentText("산책 중..")
            .setSmallIcon(R.drawable.ic_walk)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    fun stopService() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        locationList.clear()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
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

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            //todo
        }

    }

    data class LatLng(
        private val latitude: Double,
        private val longitude: Double
    )
    private fun Location.toLatLng() = LatLng(latitude = latitude, longitude = longitude)

    inner class LocalBinder: Binder() {
        fun getService(): LocationService = this@LocationService
    }

    companion object {
        private const val CHANNEL_ID = "LocationServiceChannel"
        private const val NOTIFICATION_ID = 1
    }
}