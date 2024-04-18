package com.example.reactiveapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface DaoRun {

    @Upsert
    suspend fun insertRun(run:Run)

    @Delete
    suspend fun deleteRun(run:Run)

    @Query("SELECT * from running_table ORDER BY timeStamp DESC")
    fun getAllRunSortedByDate():LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY timeMills DESC")
    fun getAllRunSortedByTimeInMills():LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY caloriesBurned DESC")
    fun getAllRunSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunSortedByAvgSpeed():LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER BY distanceInMeters DESC")
    fun getAllRunSortedByDistance():LiveData<List<Run>>



    @Query("SELECT SUM(timeMills) from running_table")
    fun getTotalTimeInMills():LiveData<Long>

    @Query("SELECT SUM(distanceInMeters) from running_table")
    fun getTotalDistance():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) from running_table")
    fun getTotalCaloriesBurned():LiveData<Long>

    @Query("SELECT AVG(avgSpeedInKMH) from running_table")
    fun getTotalAvgSpeed():LiveData<Long>






}