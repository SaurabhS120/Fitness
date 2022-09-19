package com.example.dummy

import com.example.domain.FitnessRepo

class FitnessDummyRepo : FitnessRepo {
    private var setSteps: ((steps: Int) -> Unit)? = null
    private var setCalories: ((calories: Int) -> Unit)? = null
    private var setHeartPoints: ((calories: Int) -> Unit)? = null
    private var stepsCount = 1
    override fun requestGoogleFitPermissions() {
        readStepsCount()
        readCaloriesCount()
        readHeartPointsCount()
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
}