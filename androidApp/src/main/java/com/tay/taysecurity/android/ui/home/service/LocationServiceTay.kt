package com.tay.taysecurity.android.ui.home.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log

class LocationServiceTay : Service(){

    val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
            val channelID = "my_channel_01"
            val channel = NotificationChannel(
                channelID,
                "Ubicacion Aleatoria",
                NotificationManager.IMPORTANCE_HIGH
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notification: Notification = Notification.Builder(this, channelID)
                .setContentTitle("Ubicacion Aleatoria")
                .setContentText("Se creo una ubicacion aleatoria").build()
            startForeground(1, notification)
        workerGpsTayFace()
        Log.d("faceGpstAy","servicio iniciado")

    }

    private  fun workerGpsTayFace(){
        handler.postDelayed({
            loadGps()
            workerGpsTayFace()
            Log.d("faceGpstAy","workerGpsTayFace")
        },1000)
    }

    @SuppressLint("WrongConstant")
    private fun loadGps(){
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE

        val mocLocationProvider =
            LocationManager.GPS_PROVIDER //lm.getBestProvider( criteria, true );

        lm.addTestProvider(
            mocLocationProvider,
            false,
            false,
            false,
            false,
            false,
            true,
            true,
            3,
            1
        )
        lm.setTestProviderEnabled(mocLocationProvider, true)
        val newLocation = Location(mocLocationProvider)
        newLocation.latitude = -26.902038
        newLocation.longitude = -48.671337
        newLocation.altitude = 3.0
        newLocation.time = System.currentTimeMillis()
        newLocation.speed = 0.01f
        newLocation.bearing = 1f
        newLocation.accuracy = 3f
        newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        newLocation.bearingAccuracyDegrees = 0.1f
        newLocation.verticalAccuracyMeters = 0.1f
        newLocation.speedAccuracyMetersPerSecond = 0.01f
        lm.setTestProviderEnabled(mocLocationProvider, true)
        lm.setTestProviderLocation(mocLocationProvider, newLocation)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null);
        Log.d("faceGpstAy","servicio onDestroy")
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}