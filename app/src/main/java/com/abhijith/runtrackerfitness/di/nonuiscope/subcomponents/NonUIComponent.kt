package com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents

import com.abhijith.runtrackerfitness.services.helpers.MapServices
import com.abhijith.runtrackerfitness.services.helpers.ServiceNotification
import com.abhijith.runtrackerfitness.services.helpers.Tracker
import dagger.Subcomponent

//@CustomScope
@Subcomponent
interface NonUIComponent {

    @Subcomponent.Factory
    interface Factory {
        fun build(): NonUIComponent
    }

    fun inject(cls: ServiceNotification)
    fun inject(cls: MapServices)
    fun inject(cls: Tracker)
}