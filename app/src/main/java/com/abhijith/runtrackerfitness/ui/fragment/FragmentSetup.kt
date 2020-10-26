package com.abhijith.runtrackerfitness.ui.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abhijith.runtrackerfitness.BaseApplication
import com.abhijith.runtrackerfitness.R
import com.abhijith.runtrackerfitness.di.nonuiscope.Test
import com.abhijith.runtrackerfitness.di.nonuiscope.Test2
import com.abhijith.runtrackerfitness.di.scope.UIScope
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.KEY_FIRST_TIME_TOGGLE
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.KEY_NAME
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

//@AndroidEntryPoint
@UIScope
class FragmentSetup : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var firstTimeAppOpen: Boolean = true

    @Inject
    lateinit var test:Test

    @Inject
    lateinit var test2: Test2

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BaseApplication.me.startConsumingFromFragmentModule(this)
        Log.e("abhi",test.String)
        Log.e("abhi",test2.String)
        BaseApplication.me.startConsumingFromFragmentModule(this)
        Log.e("abhi",test.String)
        Log.e("abhi",test2.String)
        BaseApplication.me.startConsumingFromFragmentModule(this)
        Log.e("abhi",test.String)
        Log.e("abhi",test2.String)
        if (!firstTimeAppOpen) {

            val navOptions =
                NavOptions.Builder()
                    .setPopUpTo(R.id.setupFragment2, true)
                    .build()

            findNavController().navigate(
                R.id.action_setupFragment2_to_runFragment2,
                savedInstanceState,
                navOptions
            )
        }

        tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment2_to_runFragment2)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields.", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Saves the name and the weight in shared preferences
     */
    private fun writePersonalDataToSharedPref(): Boolean {
        val name = etName.text.toString()
        val weightText = etWeight.text.toString()
        if (name.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }

}