package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val TAG = "Fitness"
    lateinit var button:Button
    lateinit var steps_tv:TextView
    val googleFitHelper = GoogleFitHelper(this,{text->
        steps_tv.setText(text)
    })

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        steps_tv = findViewById(R.id.steps_tv)

        googleFitHelper.requestGoogleFitPermissions()

        button.setOnClickListener {
            googleFitHelper.readStepsCount()
        }
    }
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