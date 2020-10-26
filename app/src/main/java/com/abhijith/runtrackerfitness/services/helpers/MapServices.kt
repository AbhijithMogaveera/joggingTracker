package com.abhijith.runtrackerfitness.services.helpers

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.abhijith.runtrackerfitness.BaseApplication
import com.abhijith.runtrackerfitness.helpers.Constants
import com.abhijith.runtrackerfitness.extention.LocCB
import com.abhijith.runtrackerfitness.extention.mGetLatLong
import com.abhijith.runtrackerfitness.services.PolyLines
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import javax.inject.Inject

@SuppressLint("MissingPermission", "LogNotTimber")
class MapServices : LocationCallback() {

    @Inject
    lateinit var locProvider: FusedLocationProviderClient

    val mldPathPoints = MutableLiveData<PolyLines>()

    var locCB = LocCB {

    }

    init {
        mldPathPoints.postValue(mutableListOf())
        BaseApplication.me.startConsumingFromMapServiceModule(this)
    }

    fun addPath(location: Location?) {

        location?.let {

            mldPathPoints.value?.apply {

                last().add(it.mGetLatLong())
                mldPathPoints.postValue(this)

            }

        }

    }

    fun addEmptyPolyLine() {

        mldPathPoints.value?.apply {

            add(mutableListOf())
            mldPathPoints.postValue(this)

        } ?: mldPathPoints.postValue(mutableListOf(mutableListOf()))

    }

    fun listenLocationCallBack(callBack: (List<Location>) -> Unit) {
        locCB.callback = callBack
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        val request = LocationRequest().apply {

            interval = Constants.LOCATION_UPDATE_INTERVAL
            fastestInterval = Constants.FASTEST_LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        locProvider.requestLocationUpdates(
            request,
            locCB,
            Looper.getMainLooper()
        )
    }


    fun stopRequestingLocationUpdates() {
        locProvider.removeLocationUpdates(locCB)
    }

}
