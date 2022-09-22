package com.example.dummy

import com.example.domain.FitnessDataType
import com.example.domain.FitnessRepo

class FitnessDummyRepo : FitnessRepo {
    private var setSteps: ((steps: Int) -> Unit)? = null
    private var setCalories: ((calories: Int) -> Unit)? = null
    private var setHeartPoints: ((heartPoints: Int) -> Unit)? = null
    private var setDistanceWalked: ((distanceWalked: Int) -> Unit)? = null
    private var setMoveMin: ((moveMin: Int) -> Unit)? = null
    private var setHeartBtHistory: ((setHeartBtHistory: List<Int>) -> Unit)? = null
    private var stepsCount = 1

    override fun requestGoogleFitPermissions() {
        FitnessDataType.values().forEach {
            readData(it)
        }
    }

    override fun setOnStepsChange(setSteps: (steps: Int) -> Unit) {
        this.setSteps = setSteps
    }

    override fun setOnCaloriesChange(setCalories: (calories: Int) -> Unit) {
        this.setCalories = setCalories
    }

    override fun setOnHeartPointsChange(setHeartPoints: (steps: Int) -> Unit) {
        this.setHeartPoints = setHeartPoints
    }

    override fun setOnDistanceWalked(setDistanceWalked: (distanceWalked: Int) -> Unit) {
        this.setDistanceWalked = setDistanceWalked
    }

    override fun setOnMoveMin(setMoveMin: (moveMin: Int) -> Unit) {
        this.setMoveMin = setMoveMin
    }

    override fun readHeartBtHistory() {
        val dummy_data = listOf(43, 52, 23, 53, 37, 42, 41)
        setHeartBtHistory?.invoke(dummy_data)
    }

    override fun setOnHeartBtHistory(setHeartBtHistory: (setHeartBtHistory: List<Int>) -> Unit) {
        this.setHeartBtHistory = setHeartBtHistory
    }

    override fun readData(fitnessDataType: FitnessDataType) {
        stepsCount++
        when (fitnessDataType) {
            FitnessDataType.STEP -> setSteps?.invoke(stepsCount)
            FitnessDataType.HEART_POINTS -> setHeartPoints?.invoke(stepsCount)
            FitnessDataType.CALORIES -> setCalories?.invoke(stepsCount)
            FitnessDataType.DISTANCE_COVERED -> setDistanceWalked?.invoke(stepsCount)
            FitnessDataType.MOVE_MIN -> setMoveMin?.invoke(stepsCount)
        }
        readHeartBtHistory()
    }
}