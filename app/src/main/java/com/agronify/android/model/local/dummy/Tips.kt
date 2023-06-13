package com.agronify.android.model.local.dummy

data class Tips(
    val weather: String,
    val tips: List<Tip>
)

data class Tip(
    val type: String,
    val content: String
)