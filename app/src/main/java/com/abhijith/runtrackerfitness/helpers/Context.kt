package com.abhijith.runtrackerfitness.helpers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.abhijith.runtrackerfitness.services.TrackingService

fun Context.mGetPauseIntent():Intent{
   return Intent(this, TrackingService::class.java)
       .apply {
           action = Constants.ACTION_PAUSE_SERVICE
       }
}

fun Context.mGetStartOrResumeIntent():Intent{
    return Intent(this, TrackingService::class.java).apply {
        action = Constants.ACTION_START_OR_RESUME_SERVICE
    }
}

fun Context.mGetNotificationManagerInstance(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}