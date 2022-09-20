package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivityFitnessBinding

class MainActivity : AppCompatActivity() {
    private val TAG = "Fitness"
    private lateinit var viewModel: FitnessViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFitnessBinding.inflate(LayoutInflater.from(this),null,false)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        val viewModel: FitnessViewModel by viewModels()
        this.viewModel = viewModel
        binding.viewModel = viewModel
        viewModel.setActivity(this)

        binding.button.setOnClickListener {
            viewModel.updateData()
        }

        val lineChartController = LineChartController(binding.chart)
        viewModel.heartBtHistory.observe(this) { heartBtHistory ->
            lineChartController.drawLineChart(heartBtHistory)
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