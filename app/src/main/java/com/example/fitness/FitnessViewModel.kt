package com.example.fitness

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.FitnessRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitnessViewModel:ViewModel() {
    private val _stepsLiveData = MutableLiveData<String>()
    private val _caloriesLiveData = MutableLiveData<String>()
    private val _heartPointsLiveData = MutableLiveData<String>()
    private val _walkedDistanceLiveData = MutableLiveData<String>()
    private val _moveMinLiveData = MutableLiveData<String>()
    val stepsLiveData= _stepsLiveData as LiveData<String>
    val caloriesLiveData = _caloriesLiveData as LiveData<String>
    val heartPointsLiveData = _heartPointsLiveData as LiveData<String>
    val walkedDistanceLiveData = _walkedDistanceLiveData as LiveData<String>
    val moveMinLiveData = _moveMinLiveData as LiveData<String>
    private lateinit var fitnessRepo: FitnessRepo
    fun setActivity(activity: Activity){
        fitnessRepo = Repo.fitnessRepo(activity)
        fitnessRepo.setOnStepsChange { steps->
            _stepsLiveData.postValue("Total steps: $steps steps")
        }
        fitnessRepo.setOnCaloriesChange { calories->
            _caloriesLiveData.postValue("$calories Kcal")
        }
        fitnessRepo.setOnHeartPointsChange { heartPoints->
            _heartPointsLiveData.postValue("Total heart points : $heartPoints heart pts")
        }
        fitnessRepo.setOnDistanceWalked { walked_distance->
            _walkedDistanceLiveData.postValue("Total distance walked : $walked_distance meters")
        }
        fitnessRepo.setOnMoveMin { move_min->
            _moveMinLiveData.postValue("Moved Mins : $move_min move min")
        }
        fitnessRepo.requestGoogleFitPermissions()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData(){
        viewModelScope.launch(Dispatchers.IO){
            fitnessRepo.readStepsCount()
            fitnessRepo.readCaloriesCount()
            fitnessRepo.readHeartPointsCount()
            fitnessRepo.readDistenceWalkedCount()
            fitnessRepo.readMoveMin()
        }
    }
}