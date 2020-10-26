package com.abhijith.runtrackerfitness.repositories

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.abhijith.runtrackerfitness.repositories.MainRepository
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(mainRepository: MainRepository) : ViewModel() {
    var totalDistance = mainRepository.getTotalDistance()
    var totalTimeInMillis = mainRepository.getTotalTimeInMillis()
    var totalAvgSpeed = mainRepository.getTotalAvgSpeed()
    var totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    var runsSortedByDate = mainRepository.getAllRunsSortedByDate()
}