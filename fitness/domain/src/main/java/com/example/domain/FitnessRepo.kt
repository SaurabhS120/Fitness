package com.example.domain

import androidx.annotation.RequiresApi

interface FitnessRepo {
    fun requestGoogleFitPermissions()
    fun readStepsCount()
    fun setOnStepsChange(setSteps:(steps:Int)->Unit)
    fun setOnCaloriesChange(setCalories: (calories: Int) -> Unit)
    @RequiresApi(value = 26)
    fun readCaloriesCount()
}