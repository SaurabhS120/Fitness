package com.example.domain

import androidx.annotation.RequiresApi

interface FitnessRepo {
    fun requestGoogleFitPermissions()
    @RequiresApi(value = 26)
    fun readStepsCount()
    fun setOnStepsChange(setSteps:(steps:Int)->Unit)
    fun setOnCaloriesChange(setCalories: (calories: Int) -> Unit)
    @RequiresApi(value = 26)
    fun readCaloriesCount()
    fun setOnHeartPointsChange(setHeartPoints: (steps: Int) -> Unit)
    @RequiresApi(value = 26)
    fun readHeartPointsCount()
}