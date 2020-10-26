package com.abhijith.runtrackerfitness.services.helpers

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.abhijith.runtrackerfitness.BaseApplication
import com.abhijith.runtrackerfitness.R
import com.abhijith.runtrackerfitness.extention.mBuildNotificationChannel
import com.abhijith.runtrackerfitness.extention.notify
import com.abhijith.runtrackerfitness.ui.notification.NotificationProvider
import com.abhijith.runtrackerfitness.helpers.Constants
import com.abhijith.runtrackerfitness.helpers.mGetNotificationManagerInstance
import com.abhijith.runtrackerfitness.helpers.mGetPauseIntent
import com.abhijith.runtrackerfitness.helpers.mGetStartOrResumeIntent
import javax.inject.Inject

class ServiceNotification(val context: Context) {

    @Inject
    lateinit var baseNotificationBuilder: NotificationProvider

    val id = Constants.NOTIFICATION_ID
    var curNotification: NotificationCompat.Builder

    init {
        BaseApplication.me.startConsumingFromMapServiceModule(this)
        curNotification = baseNotificationBuilder.baseNotificationBuilder
        context.applicationContext as BaseApplication
        val notificationManager = context.mGetNotificationManagerInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.mBuildNotificationChannel()
        }
    }

    fun changeStateToPause() {
        val notificationActionText = Constants.PAUSE
        val pauseIntent = context.mGetPauseIntent()
        val pendingIntent =
            PendingIntent.getService(
                context,
                1,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        postNotificationUpdate(notificationActionText, pendingIntent)
    }

    fun changeStateToResume() {
        val notificationActionText = Constants.RESUME
        val resumeIntent = context.mGetStartOrResumeIntent()
        val pendingIntent =
            PendingIntent.getService(
                this.context,
                2,
                resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        postNotificationUpdate(notificationActionText, pendingIntent)

    }

    fun updateText(string: String) {
        val notification = curNotification.setContentText(string)
        context.mGetNotificationManagerInstance().notify(notification)
    }

    private fun postNotificationUpdate(
        notificationActionText: String,
        pendingIntent: PendingIntent?
    ) {
        val notificationManager = context.mGetNotificationManagerInstance()
        curNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotification, ArrayList<NotificationCompat.Action>())
        }
        curNotification =
            baseNotificationBuilder
                .baseNotificationBuilder
                .addAction(
                    R.drawable.ic_pause_black_24dp,
                    notificationActionText,
                    pendingIntent
                )

        notificationManager.notify(curNotification)
    }
}