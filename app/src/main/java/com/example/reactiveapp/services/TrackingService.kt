package com.example.reactiveapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.reactiveapp.R
import com.example.reactiveapp.common.Constants.ACTION_PAUSE_SERVICE
import com.example.reactiveapp.common.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.reactiveapp.common.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.reactiveapp.common.Constants.ACTION_STOP_SERVICE
import com.example.reactiveapp.common.Constants.FASTEST_LOCATION_INTERVAL
import com.example.reactiveapp.common.Constants.LOCATION_UPDATE_INTERVAL
import com.example.reactiveapp.common.Constants.NOTIFICATION_CHANNEL_ID
import com.example.reactiveapp.common.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.reactiveapp.common.Constants.NOTIFICATION_ID
import com.example.reactiveapp.common.TrackingUtility
import com.example.reactiveapp.ui.MainActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import timber.log.Timber
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


typealias Polyline=MutableList<LatLng>
typealias Polylines=MutableList<Polyline>

class TrackingService: LifecycleService(){

    var isFirstRun=true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        val isTracking=MutableLiveData<Boolean>()
        val pathPoints=MutableLiveData<Polylines>()
    }

    private fun postInitialValue(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValue()
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE-> {

                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                        Timber.d("Running service...")

                    }else{
                        Timber.d("Resuming service...")
                        startForegroundService()

                    }

                }
                ACTION_PAUSE_SERVICE-> pauseService()
                ACTION_STOP_SERVICE-> Timber.d("stop service")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    val locationCallback=object :LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!){
                result.locations.let {locations->
                    for (location in locations){
                        addPathPoint(location)
                        Timber.d("new Location, ${location.latitude} ${location.longitude}")
                        Toast.makeText(this@TrackingService,"${location.latitude} ${location.longitude}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun pauseService(){
        isTracking.postValue(false)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if (isTracking){
            if (TrackingUtility.hasLocationPermissions(this)){

                //deprecated
//                val request = LocationRequest.create().apply {
//                    interval = LOCATION_UPDATE_INTERVAL
//                    fastestInterval = FASTEST_LOCATION_INTERVAL
//                    priority = PRIORITY_HIGH_ACCURACY
//                }

                val request=LocationRequest.Builder(PRIORITY_HIGH_ACCURACY,LOCATION_UPDATE_INTERVAL,)
                    .setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )


            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }





    private fun addPathPoint(location:Location?){
        location?.let {
            val pos=LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)

            }
        }
    }

    private fun addEmptyPolyLine()= pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?:pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun startForegroundService(){

        addEmptyPolyLine()
        isTracking.postValue(true)

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder=NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent()=PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action= ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_MUTABLE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
            )

        notificationManager.createNotificationChannel(channel)
    }

}