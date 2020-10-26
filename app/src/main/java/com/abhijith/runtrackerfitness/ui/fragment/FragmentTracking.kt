package com.abhijith.runtrackerfitness.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abhijith.runtrackerfitness.BaseApplication
import com.abhijith.runtrackerfitness.R
import com.abhijith.runtrackerfitness.db.models.Run
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_PAUSE_SERVICE
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.ACTION_STOP_SERVICE
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.CANCEL_DIALOG_TAG
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.MAP_VIEW_BUNDLE_KEY
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.MAP_ZOOM
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.POLYLINE_COLOR
import com.abhijith.runtrackerfitness.helpers.Constants.Companion.POLYLINE_WIDTH
import com.abhijith.runtrackerfitness.helpers.TrackingUtility
import com.abhijith.runtrackerfitness.repositories.MainViewModel
import com.abhijith.runtrackerfitness.services.PolyLines
import com.abhijith.runtrackerfitness.services.TrackingService
import com.abhijith.runtrackerfitness.ui.view.CancelRunDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round


//@AndroidEntryPoint
class FragmentTracking : Fragment(R.layout.fragment_tracking) {

    @set:Inject
    var weight: Float = 80f

    private var map: GoogleMap? = null

    private var isTracking = false
    private var curTimeInMillis = 0L
    private var pathPoints = mutableListOf<MutableList<LatLng>>()

    @Inject
    lateinit var viewModel: MainViewModel
//            by viewModels()

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        BaseApplication.me.startConsumingFromFragmentModule(this)

        mapView.onCreate(mapViewBundle)

        // restore dialog instance

        if (savedInstanceState != null) {
            val cancelRunDialog =
                parentFragmentManager.findFragmentByTag(CANCEL_DIALOG_TAG) as CancelRunDialog?
            cancelRunDialog?.setYesListener {
                stopRun()
            }
        }

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        btnFinishRun.setOnClickListener {
            zoomToWholeTrack()
            endRunAndSaveToDB()
        }

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    /**
     * Subscribes to changes of LiveData objects
     */
    private fun subscribeToObservers() {

        Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()

        val call = object : TrackingService.CallBack{
            override fun onTrackingStateChanged(state: Boolean) {
                updateTracking(state)
            }

            override fun onNewPathPoints(polyLines: PolyLines) {
                pathPoints = polyLines
                addLatestPolyline()
                moveCameraToUser()
            }

            override fun onTotalDistanceChanged(distance: Long) {
                curTimeInMillis = distance
                val formattedTime = TrackingUtility.getFormattedStopWatchTime(distance, true)
                tvTimer.text = formattedTime
            }
        }
        TrackingService.callback = call

//        TrackingService.trackingLiveUpdates = {
//            updateTracking(it)
//        }
//
//        TrackingService.pathPointsLiveUpdates = {
//            pathPoints = it
//            addLatestPolyline()
//            moveCameraToUser()
//        }
//
//
//        TrackingService.timeRunInMillis = {
//            curTimeInMillis = it
//            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it, true)
//            tvTimer.text = formattedTime
//        }

    }

    /**
     * Will move the camera to the user's location.
     */
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    /**
     * Adds all polylines to the pathPoints list to display them after screen rotations
     */
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    /**
     * Draws a polyline between the two latest points.
     */
    private fun addLatestPolyline() {
        // only add polyline if we have at least two elements in the last polyline
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    /**
     * Updates the tracking variable and the UI accordingly
     */
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        try {
            if (!isTracking && curTimeInMillis > 0L) {
                btnToggleRun.text = getString(R.string.start)
                btnFinishRun.visibility = View.VISIBLE
            } else if (isTracking) {
                btnToggleRun.text = getString(R.string.stop)
                menu?.getItem(0)?.isVisible = true
                btnFinishRun.visibility = View.GONE
            }
        }catch (e:IllegalStateException) {

        }finally {

        }
    }

    /**
     * Toggles the tracking state
     */
    @SuppressLint("MissingPermission")
    private fun toggleRun() {
        if (isTracking) {
            menu
                ?.getItem(0)
                ?.isVisible = true
            pauseTrackingService()
        } else {
            startOrResumeTrackingService()
            Timber.d("Started service")
        }
    }

    /**
     * Starts the tracking service or resumes it if it is currently paused.
     */
    private fun startOrResumeTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_START_OR_RESUME_SERVICE
            requireContext()
                .startService(it)
        }

    /**
     * Pauses the tracking service
     */
    private fun pauseTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_PAUSE_SERVICE
            requireContext()
                .startService(it)
        }

    /**
     * Stops the tracking service.
     */
    private fun stopTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_STOP_SERVICE
            requireContext().startService(it)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        val mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    /**
     * Zooms out until the whole track is visible. Used to make a screenshot of the
     * MapView to save it in the database
     */
    private fun zoomToWholeTrack() {

        val bounds = LatLngBounds.Builder()

        for (polyline in pathPoints) {
            for (point in polyline) {
                bounds.include(point)
            }
        }

        val width = mapView.width
        val height = mapView.height

        try {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    width,
                    height,
                    (height * 0.05f).toInt()
                )
            )
        } catch (e: Exception) {

        }

    }

    /**
     * Saves the recent run in the Room database and ends it
     */
    private fun endRunAndSaveToDB() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val timestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run =
                Run(bmp, timestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully.",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    /**
     * Finishes the tracking.
     */
    private fun stopRun() {
        Timber.d("STOPPING RUN")
        tvTimer.text = "00:00:00:00"
        stopTrackingService()
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment2)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu_tracking, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // just checking for isTracking doesn't trigger this when rotating the device
        // in paused mode
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    /**
     * Shows a dialog to cancel the current run.
     */
    private fun showCancelTrackingDialog() {
        CancelRunDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_DIALOG_TAG)
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}