package com.example.fitness

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.FitnessDataType
import com.example.domain.FitnessRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FitnessViewModel(application: Application) : AndroidViewModel(application) {
    val df = SimpleDateFormat(
        "E, dd MMM yyyy",
        Locale.getDefault()
    ) // pass the format pattern that you like and done.
    val date = df.format(Date())
    private val _stepsLiveData = MutableLiveData<String>()
    private val _caloriesLiveData = MutableLiveData<String>()
    private val _heartPointsLiveData = MutableLiveData<String>()
    private val _walkedDistanceLiveData = MutableLiveData<String>()
    private val _moveMinLiveData = MutableLiveData<String>()
    val _heartBtHistory = MutableLiveData<List<Int>>()
    val stepsLiveData = _stepsLiveData as LiveData<String>
    val caloriesLiveData = _caloriesLiveData as LiveData<String>
    val heartPointsLiveData = _heartPointsLiveData as LiveData<String>
    val walkedDistanceLiveData = _walkedDistanceLiveData as LiveData<String>
    val moveMinLiveData = _moveMinLiveData as LiveData<String>
    val heartBtHistory = _heartBtHistory as LiveData<List<Int>>
    private lateinit var fitnessRepo: FitnessRepo
    private val localRepo = Repo.localRepo(application.applicationContext)
    fun setActivity(activity: Activity){
        fitnessRepo = Repo.fitnessRepo(activity)
        fitnessRepo.setOnStepsChange { steps->
            _stepsLiveData.postValue("$steps")
        }
        fitnessRepo.setOnCaloriesChange { calories->
            _caloriesLiveData.postValue("$calories Kcal")
        }
        fitnessRepo.setOnHeartPointsChange { heartPoints ->
            _heartPointsLiveData.postValue("$heartPoints heart pts")
        }
        fitnessRepo.setOnDistanceWalked { walked_distance ->
            _walkedDistanceLiveData.postValue("$walked_distance m")
        }
        fitnessRepo.setOnMoveMin { move_min ->
            _moveMinLiveData.postValue("$move_min")
        }
        fitnessRepo.setOnStepsHistory { heartBtHistory ->
            _heartBtHistory.postValue(heartBtHistory)
        }
        fitnessRepo.requestGoogleFitPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            FitnessDataType.values().forEach {
                fitnessRepo.readData(it)
            }
            fitnessRepo.readStepsHistory()
        }
    }

    fun isFirstLaunch(): Boolean {
        return localRepo.isFirstLaunch()
    }
}