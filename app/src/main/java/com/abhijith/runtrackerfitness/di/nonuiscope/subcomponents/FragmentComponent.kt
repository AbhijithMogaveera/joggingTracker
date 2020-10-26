package com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents

import com.abhijith.runtrackerfitness.ui.fragment.*
import com.abhijith.runtrackerfitness.ui.fragment.FragmentSettings
import com.abhijith.runtrackerfitness.ui.fragment.FragmentSetup
import com.abhijith.runtrackerfitness.ui.fragment.RunFragment
import dagger.Subcomponent

@Subcomponent
interface FragmentComponent {

    @Subcomponent.Factory
    interface Factory{
        fun build(): FragmentComponent
    }

    fun inject(cls: RunFragment)
    fun inject(cls: FragmentSettings)
    fun inject(cls: FragmentSetup)
    fun inject(cls:FragmentStatistics)
    fun inject(cls:FragmentTracking)
}