package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.domain.FitnessRepo
import com.example.google_fit.GoogleFitFitnessRepo

class MainActivity : AppCompatActivity() {
    private val TAG = "Fitness"
    private lateinit var button:Button
    private lateinit var steps_tv:TextView
    private lateinit var calories_tv:TextView
    private lateinit var heartPoints_tv:TextView
    private val fitnessRepo: FitnessRepo =
        Repo.fitnessRepo(this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        steps_tv = findViewById(R.id.steps_tv)
        calories_tv = findViewById(R.id.calories_tv)
        heartPoints_tv = findViewById(R.id.heart_points_tv)
        fitnessRepo.setOnStepsChange { steps->
            steps_tv.setText("Total steps: $steps")
        }
        fitnessRepo.setOnCaloriesChange { calories->
            calories_tv.setText("Total calories : $calories")
        }
        fitnessRepo.setOnHeartPointsChange { heartPoints->
            heartPoints_tv.setText("Total heart points : $heartPoints")
        }
        fitnessRepo.requestGoogleFitPermissions()

        button.setOnClickListener {
            fitnessRepo.readStepsCount()
            fitnessRepo.readCaloriesCount()
            fitnessRepo.readHeartPointsCount()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                1 -> {
                    Log.d(TAG,"Login success")

                }
                else -> {
                    // Result wasn't from Google Fit
                    Log.d(TAG,"Result wasn't from Google Fit")
                }
            }
            else -> {
                // Permission not granted
                Log.d(TAG,"Permission not granted")
            }
        }
    }
}