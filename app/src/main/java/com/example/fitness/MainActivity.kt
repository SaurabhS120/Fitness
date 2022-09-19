package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.domain.FitnessRepo
import com.example.fitness.databinding.ActivityMainBinding
import com.example.google_fit.GoogleFitFitnessRepo

class MainActivity : AppCompatActivity() {
    private val TAG = "Fitness"
    private lateinit var viewModel: FitnessViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this),null,false)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        val viewModel: FitnessViewModel by viewModels()
        this.viewModel = viewModel
        binding.viewModel = viewModel
        viewModel.setActivity(this)

        binding.button.setOnClickListener {
            viewModel.updateData()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                1 -> {
                    Log.d(TAG,"Login success")
                    viewModel.updateData()
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