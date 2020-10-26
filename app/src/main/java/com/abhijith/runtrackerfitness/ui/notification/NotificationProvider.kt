package com.abhijith.runtrackerfitness.ui.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.abhijith.runtrackerfitness.R
import com.abhijith.runtrackerfitness.di.scope.RunningAppComponentScope
import com.abhijith.runtrackerfitness.helpers.Constants
import com.abhijith.runtrackerfitness.ui.activity.MainActivity
import javax.inject.Inject

@RunningAppComponentScope
class NotificationProvider
@Inject constructor(context: Context) {

    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
    }

    val baseNotificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)
}