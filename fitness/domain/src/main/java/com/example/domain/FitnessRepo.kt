package com.example.domain

interface FitnessRepo {
    fun requestGoogleFitPermissions()
    fun readStepsCount()
}