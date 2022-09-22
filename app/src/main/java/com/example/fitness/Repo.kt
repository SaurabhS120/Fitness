package com.example.fitness

import android.app.Activity
import android.content.Context
import com.example.domain.FitnessRepo
import com.example.fitness.data.LocalSpRepo
import com.example.fitness.domain.LocalRepo
import com.example.google_fit.GoogleFitFitnessRepo

object Repo {
    fun fitnessRepo(activity: Activity): FitnessRepo {
        return GoogleFitFitnessRepo(activity)
    }

    fun localRepo(applicationContext: Context): LocalRepo {
        return LocalSpRepo(applicationContext)
    }
}