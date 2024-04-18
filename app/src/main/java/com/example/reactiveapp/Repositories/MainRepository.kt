package com.example.reactiveapp.Repositories

import com.example.reactiveapp.db.DaoRun
import com.example.reactiveapp.db.Run
import javax.inject.Inject

class MainRepository @Inject constructor(val runDao:DaoRun) {

    suspend fun insertRun(run:Run)=runDao.insertRun(run)
    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)



    suspend fun getAllRunsSortedByDate()=runDao.getAllRunSortedByDate()
    suspend fun getAllRunsBySortedDistance()=runDao.getAllRunSortedByDistance()
    suspend fun getAllRunsBySortedTimeInMills()=runDao.getAllRunSortedByTimeInMills()
    suspend fun getAllRunsBySortedByAvgSpeed()=runDao.getAllRunSortedByAvgSpeed()
    suspend fun getAllRunsBySortedByCaloriesBurned()=runDao.getAllRunSortedByCaloriesBurned()

    suspend fun getTotalAvgSpeed()=runDao.getTotalAvgSpeed()
    suspend fun getTotalDistance()=runDao.getTotalDistance()
    suspend fun getTotalACaloriesBurned()=runDao.getTotalCaloriesBurned()
    suspend fun getTotalTimeInMills()=runDao.getTotalTimeInMills()


}