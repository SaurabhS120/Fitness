package com.example.domain

interface FitnessRepo {
    fun requestGoogleFitPermissions()
    fun readStepsCount()
    fun setOnStepsChange(setSteps:(steps:Int)->Unit)
}