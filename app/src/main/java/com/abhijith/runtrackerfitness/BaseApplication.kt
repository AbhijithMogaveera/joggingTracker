package com.abhijith.runtrackerfitness

import android.app.Application
import com.abhijith.runtrackerfitness.di.nonuiscope.RunningAppComponent
import com.abhijith.runtrackerfitness.di.nonuiscope.DaggerRunningAppComponent
import com.abhijith.runtrackerfitness.services.helpers.MapServices
import com.abhijith.runtrackerfitness.services.helpers.ServiceNotification
import com.abhijith.runtrackerfitness.ui.activity.MainActivity
import com.abhijith.runtrackerfitness.ui.fragment.*
import com.abhijith.runtrackerfitness.ui.fragment.FragmentSettings
import com.abhijith.runtrackerfitness.ui.fragment.FragmentSetup
import com.abhijith.runtrackerfitness.ui.fragment.RunFragment
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class
BaseApplication : Application() {

    @Inject
    lateinit var name: String

    private val runningAppComponent: RunningAppComponent by lazy {
        init()
    }

    companion object {
        /*never use for other then DI*/
        lateinit var me: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        me = this@BaseApplication
        Timber.plant(Timber.DebugTree())
    }

    private fun init(): RunningAppComponent = DaggerRunningAppComponent.factory().build(this, this)

    fun startConsumingFromFragmentModule(setupFragment: FragmentSetup) {
        return runningAppComponent.getFragmentComponent().build().inject(setupFragment)
    }

    fun startConsumingFromMapServiceModule(mapServices: MapServices) {
        return runningAppComponent.getNonUiComponentFactory().build().inject(mapServices)
    }

    fun startConsumingFromMapServiceModule(serviceNotification: ServiceNotification) {
        return runningAppComponent.getNonUiComponentFactory().build().inject(serviceNotification)
    }

    fun startConsumingFromActivityModule(mainActivity: MainActivity) {
        return runningAppComponent.getActivityComponent().build().inject(mainActivity)
    }

    fun startConsumingFromFragmentModule(runFragment: RunFragment) {
        return runningAppComponent.getFragmentComponent().build().inject(runFragment)
    }

    fun startConsumingFromFragmentModule(fragmentSettings: FragmentSettings) {
        return runningAppComponent.getFragmentComponent().build().inject(fragmentSettings)
    }


    fun startConsumingFromFragmentModule(statisticsFragment: FragmentStatistics) {
        return runningAppComponent.getFragmentComponent().build().inject(statisticsFragment)
    }

    fun startConsumingFromFragmentModule(trackingFragment: FragmentTracking) {
        return runningAppComponent.getFragmentComponent().build().inject(trackingFragment)
    }


}

