package com.example.reactiveapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.reactiveapp.R
import com.example.reactiveapp.common.Constants.ACTION_PAUSE_SERVICE
import com.example.reactiveapp.common.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.reactiveapp.common.Constants.ACTION_STOP_SERVICE
import com.example.reactiveapp.common.Constants.MAP_ZOOM
import com.example.reactiveapp.common.Constants.POLYLINE_COLOR
import com.example.reactiveapp.common.Constants.POLYLINE_WIDTH
import com.example.reactiveapp.common.TrackingUtility
import com.example.reactiveapp.databinding.FragmentTrackingBinding
import com.example.reactiveapp.db.Run
import com.example.reactiveapp.services.Polyline
import com.example.reactiveapp.services.TrackingService
import com.example.reactiveapp.ui.viewModels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round
const val CANCEL_TRACKING_DIALOG_TAG="CANCEL_TRACKING_DIALOG_TAG"
@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    lateinit var binding:FragmentTrackingBinding

    private var map:GoogleMap?=null

    private var isTracking=false
    private var pathPoints= mutableListOf<Polyline>()

    private var curTimeInMillis = 0L

    var menu: Menu?=null



    @set:Inject
    private var weight = 80f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTrackingBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
       // setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)

        if (savedInstanceState!=null){
            val cancelTrackingDialog=parentFragmentManager.findFragmentByTag(CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog

            cancelTrackingDialog.setYesListener {
                stopRun()
            }
        }

        binding.btnToggleRun.setOnClickListener {

            toggleRun()
            //sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }

        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }


        binding.btnCancelRunning.setOnClickListener {
            showCancelTrackingDialog()
        }


        binding.mapView.getMapAsync {
            map=it
            addAllPolylines()
            Log.e("mapLocation",map.toString())
        }

        subscribeToObservers()
    }


    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })


        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            binding.tvTimer.text = formattedTime

            if (curTimeInMillis>0L){
                binding.btnCancelRunning.visibility=View.VISIBLE

            }

        })
    }


    private fun toggleRun() {
        if(isTracking) {
        //   menu?.getItem(0)?.isVisible=true
            binding.btnCancelRunning.visibility=View.VISIBLE
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        this.menu=menu
//    }
//
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        if (curTimeInMillis>0L){
//            this.menu?.getItem(0)?.isVisible=true
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.miCancelTracking -> {
//                showCancelTrackingDialog()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun stopRun() {
        binding.tvTimer.text="00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
     //   findNavController().popBackStack()
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }




    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking&&curTimeInMillis>0L) {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
            binding.btnCancelRunning.visibility=View.VISIBLE

            //menu?.getItem(0)?.isVisible=true

        }
    }


    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }


    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints) {
            for(pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }


    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for(polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(bmp, dateTimestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            mainViewModel.insertRun(run)
            Snackbar.make(
                binding.root,
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }


    private fun addAllPolylines() {
        for(polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .visible(true)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)


        }
    }

    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size>1){
            val preLastLatLng=pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng=pathPoints.last().last()

            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)

        }
    }



    private fun sendCommandToService(action:String){
        Intent(requireContext(),TrackingService::class.java).also {
            it.action=action
            requireContext().startService(it)
        }
    }


    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }


}