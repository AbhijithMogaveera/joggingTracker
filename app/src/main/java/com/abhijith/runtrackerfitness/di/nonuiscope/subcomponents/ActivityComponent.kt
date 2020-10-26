package com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents

import com.abhijith.runtrackerfitness.ui.activity.MainActivity
import dagger.Subcomponent

@Subcomponent
interface ActivityComponent {

    @Subcomponent.Factory
    interface Factory{
        fun build(): ActivityComponent
    }

    fun inject(cls:MainActivity)
}