package com.example.dummy

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
        readStepsCount()
        readCaloriesCount()
        readHeartPointsCount()
        readDistenceWalkedCount()
        readMoveMin()
        readHeartBtHistory()
    }

    override fun readStepsCount() {
        setSteps?.invoke(stepsCount++)
    }

    override fun setOnStepsChange(setSteps: (steps: Int) -> Unit) {
        this.setSteps = setSteps
    }

    override fun setOnCaloriesChange(setCalories: (calories: Int) -> Unit) {
        this.setCalories = setCalories
    }

    override fun readCaloriesCount() {
        setCalories?.invoke(stepsCount)
    }

    override fun setOnHeartPointsChange(setHeartPoints: (steps: Int) -> Unit) {
        this.setHeartPoints = setHeartPoints
    }

    override fun readHeartPointsCount() {
        setHeartPoints?.invoke(stepsCount)
    }

    override fun readDistenceWalkedCount() {
        setDistanceWalked?.invoke(stepsCount)
    }

    override fun setOnDistanceWalked(setDistanceWalked: (distanceWalked: Int) -> Unit) {
        this.setDistanceWalked = setDistanceWalked
    }

    override fun setOnMoveMin(setMoveMin: (moveMin: Int) -> Unit) {
        this.setMoveMin = setMoveMin
    }

    override fun readMoveMin() {
        setMoveMin?.invoke(stepsCount)
    }

    override fun readHeartBtHistory() {
        val dummy_data = listOf(43, 52, 23, 53, 37, 42, 41)
        setHeartBtHistory?.invoke(dummy_data)
    }

    override fun setOnHeartBtHistory(setHeartBtHistory: (setHeartBtHistory: List<Int>) -> Unit) {
        this.setHeartBtHistory = setHeartBtHistory
    }
}