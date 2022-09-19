package com.example.google_fit

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.domain.FitnessRepo
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

class GoogleFitFitnessRepo(val activity: Activity): FitnessRepo {

    private val TAG = "Google Fit Helper"
    private lateinit var fitnessOptions : FitnessOptions
    private var setSteps:((steps:Int)->Unit)? = null
    private var setCalories:((calores:Int)->Unit)? = null
    private var setHeartPoints:((heartPoints:Int)->Unit)? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun requestGoogleFitPermissions() {
        if (ContextCompat.checkSelfPermission(activity,"android.permission.ACTIVITY_RECOGNITION")
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(activity,"Permission is not granted", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(activity,
                arrayOf("android.permission.ACTIVITY_RECOGNITION"),
                0)
        }
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .build()
        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity, // your activity
                1, // e.g. 1
                account,
                fitnessOptions)
        } else {
            accessGoogleFit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readStepsCount() {
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    Log.d(TAG, "Active subscription for data type: ${dt?.name}")
                }
            }
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { result ->
                val totalSteps =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem getting steps.", e)
            }
        val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())

        val datasource = DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build()

        val request = DataReadRequest.Builder()
            .aggregate(datasource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readData(request)
            .addOnSuccessListener { response ->
                val totalSteps = response.buckets
                    .flatMap { it.dataSets }
                    .flatMap { it.dataPoints }
                    .sumBy { it.getValue(Field.FIELD_STEPS).asInt() }
                Log.d(TAG, "Total steps: $totalSteps")
                setSteps?.invoke(totalSteps)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readCaloriesCount() {
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    Log.d(TAG, "Active subscription for data type: ${dt?.name}")
                }
            }
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener { result ->
                val totalCalories =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_CALORIES)?.asFloat() ?: 0
                Log.d("Total Calories",totalCalories.toString())
                setCalories?.invoke(totalCalories.toInt())
                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readHeartPointsCount() {
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    Log.d(TAG, "Active subscription for data type: ${dt?.name}")
                }
            }
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_HEART_POINTS)
            .addOnSuccessListener { result ->
                val totalHeartPoints =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_INTENSITY)?.asFloat() ?: 0
                Log.d("Total Calories",totalHeartPoints.toString())
                setHeartPoints?.invoke(totalHeartPoints.toInt())
                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem getting steps.", e)
            }

    }

    override fun setOnStepsChange(setSteps: (steps: Int) -> Unit) {
        this.setSteps = setSteps
    }

    override fun setOnCaloriesChange(setCalories: (steps: Int) -> Unit) {
        this.setCalories = setCalories
    }

    override fun setOnHeartPointsChange(setHeartPoints: (steps: Int) -> Unit) {
        this.setHeartPoints = setHeartPoints
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {
        Log.d(TAG,"accessGoogleFit")
        subscribeFitness()
        subscribeCalories()
        subscribeHeartPoints()
        readStepsCount()
        readCaloriesCount()
        readHeartPointsCount()
    }

    private fun subscribeFitness(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun subscribeCalories(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun subscribeHeartPoints(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
            .subscribe(DataType.TYPE_HEART_POINTS)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem subscribing.", e)
            }
    }
}