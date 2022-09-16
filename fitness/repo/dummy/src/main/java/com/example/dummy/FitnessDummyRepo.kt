package com.example.dummy

import com.example.domain.FitnessRepo

class FitnessDummyRepo : FitnessRepo {
    private var setSteps: ((steps: Int) -> Unit)? = null
    private var stepsCount = 1
    override fun requestGoogleFitPermissions() {
        readStepsCount()
    }

    override fun readStepsCount() {
        setSteps?.invoke(stepsCount++)
    }

    override fun setOnStepsChange(setSteps: (steps: Int) -> Unit) {
        this.setSteps = setSteps
    }
}