package com.mendes_jv.leal_train.data.repositories

import com.google.firebase.Timestamp
import com.mendes_jv.leal_train.common.Result
import com.mendes_jv.leal_train.data.model.Exercise
import com.mendes_jv.leal_train.data.model.Train

interface TrainRepository {
    suspend fun addTrain(
        name: Number,
        description: String,
        data: Timestamp,
        exercises: List<Exercise>
    ): Result<Unit>

    suspend fun getAllTrains(): Result<List<Train>>

    suspend fun deleteTrain(name: Number): Result<Unit>

    suspend fun updateTrain(
        name: Number,
        description: String,
        data: Timestamp,
        exercises: List<Exercise>
    ): Result<Unit>
}
