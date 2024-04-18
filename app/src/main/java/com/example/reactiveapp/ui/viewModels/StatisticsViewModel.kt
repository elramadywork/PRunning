package com.example.reactiveapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.reactiveapp.Repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepo:MainRepository
) :ViewModel(){

}