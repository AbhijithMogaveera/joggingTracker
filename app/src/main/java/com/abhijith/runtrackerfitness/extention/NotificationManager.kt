package com.abhijith.runtrackerfitness.extention

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.abhijith.runtrackerfitness.helpers.Constants

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.mBuildNotificationChannel() {
    createNotificationChannel(
        NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
    )
}

fun NotificationManager.notify(x: NotificationCompat.Builder) {
    notify(Constants.NOTIFICATION_ID, x.build())
}

//val MutableLiveData<Boolean>.isInProgress
//    get() = value!!