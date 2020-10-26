package com.abhijith.runtrackerfitness.services

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.abhijith.runtrackerfitness.helpers.*
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_PAUSE_SERVICE
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_STOP_SERVICE
import com.abhijith.runtrackerfitness.services.helpers.MapServices
import com.abhijith.runtrackerfitness.services.helpers.ServiceNotification
import com.abhijith.runtrackerfitness.services.helpers.Tracker
//import com.abhijith.runtrackerfitness.services.helpers.isInProgress
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    companion object{
        var callback:CallBack?=null

//        var trackingLiveUpdates:(Boolean)->Unit = {}
//        var pathPointsLiveUpdates:(PolyLines)->Unit= {}
//        var timeRunInMillis:(Long)->Unit={}
    }



    interface CallBack{
        fun onTrackingStateChanged(state: Boolean)
        fun onNewPathPoints(polyLines: PolyLines)
        fun onTotalDistanceChanged(distance: Long)
    }

    private var isServiceKilled = false
    lateinit var tracker: Tracker
    lateinit var mapServices: MapServices

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var curNotification: NotificationCompat.Builder

    lateinit var  notificationService: ServiceNotification

    fun build() {
        tracker = Tracker(this)
        mapServices =  MapServices()
        notificationService = ServiceNotification(this)
        tracker
            .mldIsTracking
            .observe(this, Observer{
                runSafe {
                    callback!!.onTrackingStateChanged(it)
                }
//                trackingLiveUpdates(it)
            })

        tracker.mldTimeRunInMillis.observe(this, Observer {
            runSafe {
                callback!!.onTotalDistanceChanged(it)
            }
//            timeRunInMillis(it)
        })

        mapServices.mldPathPoints.observe(
            this,
            Observer {
                runSafe {
                    callback!!.onNewPathPoints(it)
                }
//                pathPointsLiveUpdates(it)
            }
        )

    }

    override fun onCreate() {
        super.onCreate()

        curNotification = this.baseNotificationBuilder

        build()

        mapServices.listenLocationCallBack { locationList ->
            if (tracker.mldIsTracking.value!!) {
                locationList.forEach { location ->
                    mapServices.addPath(location)
                }
            }
        }
        tracker.onTrackingStateChanged {
            updateNotificationTrackingStateToPauseIf(it)
            stopLocationCheckingIf(it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {

            when (it.action) {

                ACTION_START_OR_RESUME_SERVICE -> {
                    if (tracker.isFirstRun) {
                        startForegroundService()
                        tracker.isFirstRun = false
                        isServiceKilled = false
                    } else {
                        mapServices.addEmptyPolyLine()
                        tracker.start()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    tracker.pause()
                }

                ACTION_STOP_SERVICE -> {
                    killService()
                }

            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        isServiceKilled = true
        tracker.isFirstRun = true
        tracker.pause()
        tracker = Tracker(this)
        mapServices = MapServices()
        stopForeground(true)
        stopSelf()
    }

    @SuppressLint("MissingPermission")
    private fun stopLocationCheckingIf(isTracking: Boolean) {
        if (isTracking) {
            mapServices.requestLocationUpdates()
        } else {
            mapServices.stopRequestingLocationUpdates()
        }

    }

    private fun startForegroundService() {

        startForeground(
            notificationService.id,
            notificationService.curNotification.build()
        )

        mapServices.addEmptyPolyLine()

        tracker.start()

        tracker.mldIsTracking.postValue(true)

        tracker.observeTimeRunInMilliseconds {
            if (!isServiceKilled) {
                notificationService.updateText(
                    TrackingUtility
                        .getFormattedStopWatchTime(
                            it * 1000L
                        )
                )
            }
        }
    }

    private fun updateNotificationTrackingStateToPauseIf(isTracking: Boolean) {
        if (isTracking)
            notificationService.changeStateToPause()
        else
            notificationService.changeStateToResume()
    }

    private fun runSafe(cb:()->Unit){
        if(callback!=null){
            cb()
        }
    }
}


