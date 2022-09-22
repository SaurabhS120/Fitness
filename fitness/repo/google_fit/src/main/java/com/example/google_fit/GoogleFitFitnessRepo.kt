package com.example.google_fit

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.domain.FitnessDataType
import com.example.domain.FitnessRepo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class GoogleFitFitnessRepo(val activity: Activity): FitnessRepo {

    private val TAG = "Google Fit Helper"
    private lateinit var fitnessOptions: FitnessOptions
    private var setSteps: ((steps: Int) -> Unit)? = null
    private var setCalories: ((calores: Int) -> Unit)? = null
    private var setHeartPoints: ((heartPoints: Int) -> Unit)? = null
    private var setDistanceWalked: ((distance: Int) -> Unit)? = null
    private var setMoveMin: ((moveMin: Int) -> Unit)? = null
    private var setStepsHistory: ((setHeartBtHistory: List<Int>) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun requestGoogleFitPermissions() {
        if (ContextCompat.checkSelfPermission(activity, "android.permission.ACTIVITY_RECOGNITION")
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                activity,
                "Activity recognition Permission is not granted",
                Toast.LENGTH_LONG
            ).show()
            ActivityCompat.requestPermissions(
                activity,
                arrayOf("android.permission.ACTIVITY_RECOGNITION"),
                0
            )
        }
        if (ContextCompat.checkSelfPermission(activity,"android.permission.ACCESS_FINE_LOCATION")
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity,"Location Permission is not granted", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(activity,
                arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                0)
        }
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
            .build()
        val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        logSubscription()
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity,
                1,
                account,
                fitnessOptions
            )
        } else {
            accessGoogleFit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readData(fitnessDataType: FitnessDataType) {
        val gFitDataType = GFitDataType.convert(fitnessDataType)
        Fitness.getHistoryClient(
            activity,
            GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        )
            .readDailyTotal(gFitDataType.dataType)
            .addOnSuccessListener { gFitResult ->
                val resultAsValue =
                    gFitResult.dataPoints.firstOrNull()?.getValue(gFitDataType.field)
                val result = when (gFitDataType.incomingDataType) {
                    GFitIncomingDataTypes.INT -> resultAsValue?.asInt() ?: 0
                    GFitIncomingDataTypes.FLOAT -> resultAsValue?.asFloat()?.toInt() ?: 0
                }
                Log.i(TAG, "Total steps : $result")
                when (fitnessDataType) {
                    FitnessDataType.STEP -> setSteps?.invoke(result)
                    FitnessDataType.HEART_POINTS -> setHeartPoints?.invoke(result)
                    FitnessDataType.CALORIES -> setCalories?.invoke(result)
                    FitnessDataType.DISTANCE_COVERED -> setDistanceWalked?.invoke(result)
                    FitnessDataType.MOVE_MIN -> setMoveMin?.invoke(result)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readStepsHistory() {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        Log.i(TAG, "Range Start: $startTime")
        Log.i(TAG, "Range End: $endTime")

        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()
        Fitness.getHistoryClient(
            activity,
            GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        )
            .readData(readRequest)
            .addOnSuccessListener { response ->
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    dumpDataSet(dataSet)
                }
                val newData = response.buckets.flatMap { it.dataSets }.flatMap {
                    it.dataPoints.flatMap { dp ->
                        it.dataType.fields.map { field ->
                            Log.i(
                                TAG,
                                "\tField: ${field.name} Value: ${dp.getValue(field)}"
                            )
                            dp.getValue(field).asInt()
                        }
                    }
                }
                setStepsHistory?.invoke(newData)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e)
            }
    }

    override fun setOnStepsHistory(setStepsHistory: (setHeartBtHistory: List<Int>) -> Unit) {
        this.setStepsHistory = setStepsHistory
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

    override fun setOnDistanceWalked(setDistanceWalked: (distanceWalked: Int) -> Unit) {
        this.setDistanceWalked = setDistanceWalked
    }

    override fun setOnMoveMin(setMoveMin: (setMoveMin: Int) -> Unit) {
        this.setMoveMin = setMoveMin
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {
        Log.i(TAG, "accessGoogleFit")
        subscribeFitness()
        subscribeCalories()
        subscribeHeartPoints()
        subscribeDistanceWalked()
        subscribeMoveMin()
        FitnessDataType.values().forEach {
            readData(it)
        }
        readStepsHistory()
    }
    fun logSubscription(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                for (sc in subscriptions) {
                    val dt = sc.dataType
                    Log.i(TAG, "Active subscription for data type: ${dt?.name}")
                }
            }
    }
    private fun subscribeFitness(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun subscribeCalories(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun subscribeHeartPoints(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .subscribe(DataType.TYPE_HEART_POINTS)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun subscribeDistanceWalked(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .subscribe(DataType.TYPE_DISTANCE_DELTA)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem subscribing.", e)
            }
    }

    private fun subscribeMoveMin(){
        Fitness.getRecordingClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .subscribe(DataType.TYPE_MOVE_MINUTES)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem subscribing.", e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                Log.i(TAG, "\tField: ${field.name} Value: ${dp.getValue(field)}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()

    @RequiresApi(Build.VERSION_CODES.O)
    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()
}