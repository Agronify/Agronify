package com.agronify.android.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    private const val DATE_FORMAT = "EEEE, d MMMM yyyy"
    private const val FULL_FORMAT = "HH-mm-ss-dd-MM-yyyy"

    fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale("id", "ID"))
        return dateFormat.format(currentDate)
    }

    fun getCurrentTime(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat(FULL_FORMAT, Locale("id", "ID"))
        return dateFormat.format(currentDate)
    }

    fun getDayOrNight(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("HH", Locale("id", "ID"))
        val time = dateFormat.format(currentDate).toInt()
        return if (time in 6..17) "day" else "night"
    }
}