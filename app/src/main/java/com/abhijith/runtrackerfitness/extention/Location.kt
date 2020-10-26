package com.abhijith.runtrackerfitness.extention

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

fun Location.mGetLatLong(): LatLng = LatLng(this.latitude, this.longitude)

open class LocCB(var callback: (List<Location>) -> Unit) : LocationCallback() {
    override fun onLocationResult(p0: LocationResult?) {
        super.onLocationResult(p0)
        p0?.let {
            callback(p0.locations)
        }
    }
}