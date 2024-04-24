package com.example.reactiveapp.Repositories

import com.example.reactiveapp.db.DaoRun
import com.example.reactiveapp.db.Run
import javax.inject.Inject

class MainRepository @Inject constructor(val runDao:DaoRun) {

    suspend fun insertRun(run:Run)=runDao.insertRun(run)
    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)



     fun getAllRunsSortedByDate()=runDao.getAllRunSortedByDate()
     fun getAllRunsBySortedDistance()=runDao.getAllRunSortedByDistance()
     fun getAllRunsBySortedTimeInMills()=runDao.getAllRunSortedByTimeInMills()
     fun getAllRunsBySortedByAvgSpeed()=runDao.getAllRunSortedByAvgSpeed()
     fun getAllRunsBySortedByCaloriesBurned()=runDao.getAllRunSortedByCaloriesBurned()

     fun getTotalAvgSpeed()=runDao.getTotalAvgSpeed()
     fun getTotalDistance()=runDao.getTotalDistance()
     fun getTotalACaloriesBurned()=runDao.getTotalCaloriesBurned()
     fun getTotalTimeInMills()=runDao.getTotalTimeInMills()


}