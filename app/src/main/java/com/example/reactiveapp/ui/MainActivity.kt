package com.example.reactiveapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.reactiveapp.R
import com.example.reactiveapp.common.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.reactiveapp.common.NotificationsPermission
import com.example.reactiveapp.databinding.ActivityMainBinding
import com.example.reactiveapp.db.DaoRun
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        requestPermissionLauncher=  NotificationsPermission.requestPermission(this,binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                Log.e("errorNoti","erro2r")

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        navigateToTrackingFragmentIfNeeded(intent)

        binding.btnMain.setupWithNavController(findNavController(R.id.fragment_nav_host))

        findNavController(R.id.fragment_nav_host).addOnDestinationChangedListener{_,destination,_->

            when(destination.id){
                R.id.settingFragment,R.id.runFragment,R.id.statisticsFragment->
                    binding.btnMain.visibility= View.VISIBLE

                else->  binding.btnMain.visibility= View.GONE
            }

        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("onStartActivity","onStartActivity")

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            findNavController(R.id.fragment_nav_host).navigate(R.id.action_global_trackingFrafgment)
        }
    }


}