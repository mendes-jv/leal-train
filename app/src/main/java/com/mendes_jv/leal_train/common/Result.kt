package com.mendes_jv.leal_train.common

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}
