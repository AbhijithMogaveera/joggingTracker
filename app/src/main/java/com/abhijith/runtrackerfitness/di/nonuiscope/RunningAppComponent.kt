package com.abhijith.runtrackerfitness.di.nonuiscope

import android.app.Application
import android.content.Context
import com.abhijith.runtrackerfitness.di.scope.RunningAppComponentScope
import com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents.ActivityComponent
import com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents.FragmentComponent
import com.abhijith.runtrackerfitness.di.nonuiscope.subcomponents.NonUIComponent
import dagger.*

@RunningAppComponentScope
@Component(modules = [RunningAppModule::class])
interface RunningAppComponent {

    @Component.Factory
    interface Factory {
        fun build(
            @BindsInstance
            app:Application,
            @BindsInstance
            context: Context
        ): RunningAppComponent
    }

    fun getNonUiComponentFactory(): NonUIComponent.Factory

    fun getActivityComponent(): ActivityComponent.Factory

    fun getFragmentComponent(): FragmentComponent.Factory

}


