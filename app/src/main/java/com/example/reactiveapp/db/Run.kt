package com.example.reactiveapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("running_table")
data class Run (
    var img:Bitmap?,
    var timeStamp:Long=0L,
    var avgSpeedInKMH:Float=0f,
    var distanceInMeters:Int=0,
    var timeMills:Long=0L,
    var caloriesBurned:Int=0

){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}