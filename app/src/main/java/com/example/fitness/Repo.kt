package com.example.fitness

import android.app.Activity
import com.example.domain.FitnessRepo
import com.example.google_fit.GoogleFitFitnessRepo

object Repo {
    fun fitnessRepo(activity: Activity): FitnessRepo {
        return GoogleFitFitnessRepo(activity)
    }
}