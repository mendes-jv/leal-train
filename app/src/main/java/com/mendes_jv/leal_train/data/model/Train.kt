package com.mendes_jv.leal_train.data.model

import com.google.firebase.Timestamp

data class Train(
    val name: Number,
    val description: String,
    val data: Timestamp,
    val exercises: List<Exercise>
)
