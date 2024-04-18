package com.example.reactiveapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.reactiveapp.R
import com.example.reactiveapp.common.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.reactiveapp.common.TrackingUtility
import com.example.reactiveapp.databinding.FragmentRunBinding
import com.example.reactiveapp.ui.viewModels.MainViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val Tag: String="CasePermission"
    private val mainViewModel:MainViewModel by viewModels()
     lateinit var binding:FragmentRunBinding

//    private val backgroundLocationPermissionLauncher =
//        requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                // Background location permission granted, proceed with location-related tasks
//            } else {
//                // Background location permission denied, handle accordingly
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        lifecycleScope.launch(Dispatchers.Default) {
//            requestPermissions()
//
//        }
   //     requestPermissions()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentRunBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }


        return binding.root
    }

    @AfterPermissionGranted(1)
    private fun requestPermissions() {
        if(TrackingUtility.hasLocationPermissions(requireActivity())) {
            Log.e(Tag,"true")
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.e(Tag,"case1")

            EasyPermissions.requestPermissions(
                this.requireActivity(),
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            Log.e(Tag,"case2")
            val perms = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,

                )
           EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                 REQUEST_CODE_LOCATION_PERMISSION,
               *perms.sliceArray(0 until 2)

               )

        }
    }



    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Log.e(Tag,"OnPermissionDenied")
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            requestPermissions()
        }    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}