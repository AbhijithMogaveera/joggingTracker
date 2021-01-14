package com.abhijith.runtrackerfitness.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.abhijith.runtrackerfitness.BaseApplication
import com.abhijith.runtrackerfitness.R
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

//@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseApplication.me.startConsumingFromActivityModule(this)
        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }
        Toast.makeText(this, "name: $name", Toast.LENGTH_SHORT).show()
        navigateToTrackingFragmentIfNeeded(intent)

        if(name.isNotEmpty()) {
            val toolbarTitle = "Let's go, $name!"
            tvToolbarTitle?.text = toolbarTitle
        }

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {

                    R.id.setupFragment2, R.id.trackingFragment ->
                        bottomNavigationView.visibility = View.GONE
                    else ->
                        bottomNavigationView.visibility = View.VISIBLE
                }
            }
    }

    //Checks if we launched the activity from the notification
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment
                .findNavController()
                .navigate(
                    R.id.action_global_trackingFragment
                )
        }
    }

}