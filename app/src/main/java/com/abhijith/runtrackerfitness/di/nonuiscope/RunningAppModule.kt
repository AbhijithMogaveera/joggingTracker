package com.abhijith.runtrackerfitness.di.nonuiscope

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import com.abhijith.runtrackerfitness.R
import com.abhijith.runtrackerfitness.db.RunningDatabase
import com.abhijith.runtrackerfitness.db.dao.RunDao
import com.abhijith.runtrackerfitness.di.scope.RunningAppComponentScope
import com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents.ActivityComponent
import com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents.FragmentComponent
import com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents.NonUIComponent
import com.abhijith.runtrackerfitness.helpers.Constants
import com.abhijith.runtrackerfitness.ui.activity.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.random.Random

@Module(
    subcomponents = [
        NonUIComponent::class,
        FragmentComponent::class,
        ActivityComponent::class
    ]
)
@InstallIn(ApplicationComponent::class)
object RunningAppModule {

    @SuppressLint("VisibleForTests")
//    @Singleton
    @Provides
    fun getFusedLocationProvider(context: Context): FusedLocationProviderClient =
        FusedLocationProviderClient(context)

    //    @Singleton
    @Provides
    fun getRoomDB(app: Application): RunningDatabase = RunningDatabase(app)

    //    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase): RunDao {
        return db.getRunDao()
    }

    //    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences): String =
        sharedPreferences.getString(Constants.KEY_NAME, "") ?: ""

    //    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(Constants.KEY_WEIGHT, 80f)

    //    @Singleton
    @RunningAppComponentScope
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)


    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)

    @Provides
    fun provideActivityPendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @Provides
    @RunningAppComponentScope
    fun getTestTwo(float: Boolean) = Test("" + Random.nextInt(0, 100))

    @Provides
    fun getTestOne(float: Float) = Test2("" + Random.nextInt(0, 100))

}

class Test constructor(val String: String)

class Test2 constructor(val String: String)