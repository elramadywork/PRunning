package com.example.reactiveapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.reactiveapp.R
import com.example.reactiveapp.databinding.ActivityMainBinding
import com.example.reactiveapp.db.DaoRun
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding

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

        binding.btnMain.setupWithNavController(findNavController(R.id.fragment_nav_host))

        findNavController(R.id.fragment_nav_host).addOnDestinationChangedListener{_,destination,_->

            when(destination.id){
                R.id.settingFragment,R.id.runFragment,R.id.statisticsFragment->
                    binding.btnMain.visibility= View.VISIBLE

                else->  binding.btnMain.visibility= View.GONE
            }

        }






    }


}