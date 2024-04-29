package com.mendes_jv.leal_train.common

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentTimeAsString(): String {
    val currentTime = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentTimeString = dateFormat.format(currentTime)
    return currentTimeString
}

fun convertDateFormat(date: Timestamp): String {
    val currentDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val newDateFormat = SimpleDateFormat("EEEE MMMM yyyy, h:mma", Locale.getDefault())
    val date = currentDateFormat.parse(date.toDate().toString())
    val newDateString = newDateFormat.format(date!!)
    return newDateString
}
