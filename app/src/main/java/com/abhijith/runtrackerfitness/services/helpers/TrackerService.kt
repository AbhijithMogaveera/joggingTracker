package com.abhijith.runtrackerfitness.services.helpers

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.abhijith.runtrackerfitness.helpers.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Tracker(val lifecycleOwner: LifecycleOwner) {

    var isTimerEnabled = false
    var lapTime = 0L // time since we started the timer
    var timeRun = 0L // total time of the timer
    var timeStarted = 0L // the time when we started the timer
    var isFirstRun = true
    val mldIsTracking = MutableLiveData<Boolean>()
    val mldTimeRunInMillis = MutableLiveData<Long>()
    val mldTimeRunInSeconds = MutableLiveData<Long>()
    var lastSecondTimeStamp = 0L

    private val systemTimeInMilliseconds: Long
        get() = System.currentTimeMillis()

    @SuppressLint("LogNotTimber")
    fun start() {

        mldIsTracking.postValue(true)
        timeStarted = systemTimeInMilliseconds
        isTimerEnabled = true
        mldTimeRunInSeconds.postValue(0L)
        CoroutineScope(Dispatchers.Main).launch {
            while (mldIsTracking.value!!) {
                lapTime = systemTimeInMilliseconds - timeStarted
                mldTimeRunInMillis.postValue(timeRun + lapTime)
                Log.e("abhi","hello")
                if (0L+mldTimeRunInMillis.value!! >= lastSecondTimeStamp + 1000L) {
                        mldTimeRunInSeconds.postValue(mldTimeRunInSeconds.value!! + 1)
                        lastSecondTimeStamp += 1000L
                        Log.e("abhi","sucess")
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
        }

        timeRun += lapTime
    }

    fun pause() {
        isTimerEnabled = false
        mldIsTracking.postValue(false)
    }

    init {
        mldTimeRunInMillis.postValue(0L)
        mldIsTracking.postValue(false)
        mldTimeRunInMillis.postValue(0L)
    }

    fun observeTimeRunInMilliseconds(callback:(Long)->Unit){
        mldTimeRunInSeconds.observe(this.lifecycleOwner) {
            callback(it)
        }
    }

    fun onTrackingStateChanged(callBack:(Boolean)->Unit){
        mldIsTracking.observe(this.lifecycleOwner){
            callBack(it)
        }
    }
}