package com.example.google_fit

import com.example.domain.FitnessDataType
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field

enum class GFitDataType(
    val dataType: DataType,
    val field: Field,
    val incomingDataType: GFitIncomingDataTypes
) {
    STEP(DataType.TYPE_STEP_COUNT_DELTA, Field.FIELD_STEPS, GFitIncomingDataTypes.INT),
    HEART_POINTS(DataType.TYPE_HEART_POINTS, Field.FIELD_INTENSITY, GFitIncomingDataTypes.FLOAT),
    CALORIES(DataType.TYPE_CALORIES_EXPENDED, Field.FIELD_CALORIES, GFitIncomingDataTypes.FLOAT),
    DISTANCE_COVERED(
        DataType.TYPE_DISTANCE_DELTA,
        Field.FIELD_DISTANCE,
        GFitIncomingDataTypes.FLOAT
    ),
    MOVE_MIN(DataType.TYPE_MOVE_MINUTES, Field.FIELD_DURATION, GFitIncomingDataTypes.INT);

    companion object {
        fun convert(fitnessDataType: FitnessDataType): GFitDataType {
            return when (fitnessDataType) {
                FitnessDataType.STEP -> STEP
                FitnessDataType.HEART_POINTS -> HEART_POINTS
                FitnessDataType.CALORIES -> CALORIES
                FitnessDataType.DISTANCE_COVERED -> DISTANCE_COVERED
                FitnessDataType.MOVE_MIN -> MOVE_MIN
            }
        }
    }
}