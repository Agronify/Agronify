package com.agronify.android.util

object Constants {
    const val DEFAULT_LAT = 7.2575
    const val DEFAULT_LON = 112.7521
    const val REQUEST_CODE_PERMISSIONS = 10
    val REQUIRED_PERMISSIONS = arrayOf(
        "android.permission.CAMERA",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )
}