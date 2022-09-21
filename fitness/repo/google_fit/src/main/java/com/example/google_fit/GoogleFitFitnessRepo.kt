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
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
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
    private var setHeartBtHistory: ((setHeartBtHistory: List<Int>) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun requestGoogleFitPermissions() {
        if (ContextCompat.checkSelfPermission(activity, "android.permission.ACTIVITY_RECOGNITION")
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
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
            // Permission is not granted
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
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { result ->
                val totalSteps =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                Log.i(TAG,"Total steps : $totalSteps")
                setSteps?.invoke(totalSteps)

                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readCaloriesCount() {
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener { result ->
                val totalCalories =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_CALORIES)?.asFloat() ?: 0
                Log.i(TAG,"Total Calories $totalCalories")
                setCalories?.invoke(totalCalories.toInt())
                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readHeartPointsCount() {
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_HEART_POINTS)
            .addOnSuccessListener { result ->
                val totalHeartPoints =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_INTENSITY)?.asFloat() ?: 0
                Log.i(TAG,"Total Heart Points : $totalHeartPoints")
                setHeartPoints?.invoke(totalHeartPoints.toInt())
                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readDistenceWalkedCount() {
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
            .addOnSuccessListener { result ->
                val totalDistanceWalked =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_DISTANCE )?.asFloat() ?: 0
                Log.i(TAG,"Total distance walked $totalDistanceWalked")
                setDistanceWalked?.invoke(totalDistanceWalked.toInt())
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readMoveMin() {
        Fitness.getHistoryClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
            .readDailyTotal(DataType.TYPE_MOVE_MINUTES)
            .addOnSuccessListener { result ->
                val totalMoveMin =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_DURATION)?.asInt() ?: 0
                Log.i(TAG, "Total moved min $totalMoveMin")
                setMoveMin?.invoke(totalMoveMin)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem getting steps.", e)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun readHeartBtHistory() {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        Log.i(TAG, "Range Start: $startTime")
        Log.i(TAG, "Range End: $endTime")

        val readRequest =
            DataReadRequest.Builder()
                // The data request can specify multiple data types to return,
                // effectively combining multiple data queries into one call.
                // This example demonstrates aggregating only one data type.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                // Analogous to a "Group By" in SQL, defines how data should be
                // aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession allows
                // bucketing by <a href="/fit/android/using-sessions">sessions</a>.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()
        Fitness.getHistoryClient(
            activity,
            GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
        )
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // The aggregate query puts datasets into buckets, so flatten into a
                // single list of datasets
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    dumpDataSet(dataSet)
                }
                val newData = response.buckets.flatMap { it.dataSets }.flatMap {
                    it.dataPoints.flatMap { dp ->
                        it.dataType.fields.map { field ->
                            Log.i(
                                TAG,
                                "\tField: ${field.name.toString()} Value: ${dp.getValue(field)}"
                            )
                            dp.getValue(field).asInt()
                        }
                    }
                }
                setHeartBtHistory?.invoke(newData)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e)
            }
    }

    override fun setOnHeartBtHistory(setHeartBtHistory: (setHeartBtHistory: List<Int>) -> Unit) {
        this.setHeartBtHistory = setHeartBtHistory
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
        Log.i(TAG,"accessGoogleFit")
        subscribeFitness()
        subscribeCalories()
        subscribeHeartPoints()
        subscribeDistanceWalked()
        subscribeMoveMin()
        readStepsCount()
        readCaloriesCount()
        readHeartPointsCount()
        readDistenceWalkedCount()
        readMoveMin()
        readHeartBtHistory()
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
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
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
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
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
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
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
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
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
            // This example shows subscribing to a DataType, across all possible data
            // sources. Alternatively, a specific DataSource can be used.
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
                Log.i(TAG, "\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
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