package com.mendes_jv.leal_train.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.mendes_jv.leal_train.common.PLEASE_CHECK_INTERNET_CONNECTION
import com.mendes_jv.leal_train.common.Result
import com.mendes_jv.leal_train.common.convertDateFormat
import com.mendes_jv.leal_train.common.getCurrentTimeAsString
import com.mendes_jv.leal_train.di.IoDispatcher
import com.google.firebase.firestore.FirebaseFirestore
import com.mendes_jv.leal_train.common.TRAIN_COLLECTION_PATH_NAME
import com.mendes_jv.leal_train.data.model.Exercise
import com.mendes_jv.leal_train.data.model.Train
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class TrainRepositoryImpl @Inject constructor(
    private val lealTrainDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TrainRepository {

    override suspend fun addTrain(
        name: Number,
        description: String,
        data: Timestamp,
        exercises: List<Exercise>,
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val train = hashMapOf(
                    "name" to name,
                    "description" to description,
                    "data" to getCurrentTimeAsString(),
                    "exercises" to exercises.map { exercise ->
                        hashMapOf(
                            "name" to exercise.name,
                            "observation" to exercise.observation,
                            "image" to exercise.image,
                        )
                    },
                )

                val addTrainTimeout = withTimeoutOrNull(10000L) {
                    lealTrainDB.collection(TRAIN_COLLECTION_PATH_NAME)
                        .add(train)
                }

                if (addTrainTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllTrains(): Result<List<Train>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingTrainsTimeout = withTimeoutOrNull(10000L) {
                    lealTrainDB.collection(TRAIN_COLLECTION_PATH_NAME)
                        .get()
                        .await()
                        .documents.map { document ->
                            Train(
                                document.getLong("nome") ?: 0,
                                document.getString("descricao") ?: "",
                                document.getTimestamp("data") ?: Timestamp.now(),
                                document.get("exercicios")?.let { exercises ->
                                    (exercises as List<*>).map { exercise ->
                                        Exercise(
                                            (exercise as Map<*, *>)["nome"] as Number,
                                            exercise["imagem"] as String,
                                            exercise["observacao"] as String,
                                        )
                                    }
                                } ?: emptyList()
                            )
                        }
                }

                if (fetchingTrainsTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Log.d("TRAINS: ", "${fetchingTrainsTimeout?.toList()}")
                Result.Success(fetchingTrainsTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")
            Result.Failure(exception = exception)
        }
    }

    override suspend fun deleteTrain(name: Number): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val addTrainTimeout = withTimeoutOrNull(10000L) {
                    lealTrainDB.collection(TRAIN_COLLECTION_PATH_NAME)
                        .document(name.toString())
                        .delete()
                }

                if (addTrainTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)
                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }
                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")
            Result.Failure(exception = exception)
        }
    }

    override suspend fun updateTrain(
        name: Number,
        description: String,
        data: Timestamp,
        exercises: List<Exercise>,
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val trainUpdate: Map<String, Any> = hashMapOf(
                    "nome" to name,
                    "descricao" to description,
                    "data" to convertDateFormat(data),
                    "exercicios" to exercises.map { exercise ->
                        hashMapOf(
                            "nome" to exercise.name,
                            "observacao" to exercise.observation,
                            "imagem" to exercise.image,
                        )
                    },
                )

                val addTrainTimeout = withTimeoutOrNull(10000L) {
                    lealTrainDB.collection(TRAIN_COLLECTION_PATH_NAME)
                        .document(name.toString())
                        .update(trainUpdate)
                }

                if (addTrainTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)
                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }
                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")
            Result.Failure(exception = exception)
        }
    }
}
