package com.agronify.android.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    private const val DATE_FORMAT = "EEEE, d MMMM yyyy"

    fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale("id", "ID"))
        return dateFormat.format(currentDate)
    }
}