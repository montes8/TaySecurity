package com.tay.taysecurity.android.utils.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log

class SureGpsService:Service() {

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        val channelID = "my_channel_01"
        val channel = NotificationChannel(
            channelID,
            "Simulation gps",
            NotificationManager.IMPORTANCE_HIGH
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        val notification: Notification = Notification.Builder(this, channelID)
            .setContentTitle("Simulation gps")
            .setContentText("Simulation gps").build()
        startForeground(1, notification)
        initService()



    }


    fun initService(){
        Handler().postDelayed({
            initService()
            networkProvider()
            Log.d("TagLocationac", "Servicio simuulando...")
        },1000)
    }

    @SuppressLint("MissingPermission")
    fun networkProvider() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationManager.addTestProvider(
                LocationManager.NETWORK_PROVIDER, false, false,
                false, false, true, true, true, 0, 5
            )
        }
        locationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true)

        val mockLocation = Location(LocationManager.NETWORK_PROVIDER)
        mockLocation.latitude = -33.852 // Sydney
        mockLocation.longitude = 151.211
        mockLocation.altitude = 10.0
        mockLocation.accuracy = 5f
        mockLocation.time = System.currentTimeMillis()
        mockLocation.elapsedRealtimeNanos = System.nanoTime()
        locationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, mockLocation)


    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d("TagLocationac", "Servicio destruido...")
    }
}