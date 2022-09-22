package com.example.domain

interface FitnessRepo {
    fun requestGoogleFitPermissions()
    fun setOnStepsChange(setSteps: (steps: Int) -> Unit)
    fun setOnCaloriesChange(setCalories: (calories: Int) -> Unit)
    fun setOnHeartPointsChange(setHeartPoints: (steps: Int) -> Unit)
    fun setOnDistanceWalked(setHeartPoints: (steps: Int) -> Unit)
    fun setOnMoveMin(setMoveMin: (setMoveMin: Int) -> Unit)
    fun setOnHeartBtHistory(setHeartBtHistory: (setHeartBtHistory: List<Int>) -> Unit)
    fun readData(fitnessDataType: FitnessDataType)
    fun readHeartBtHistory()
}