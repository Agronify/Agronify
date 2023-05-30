package com.agronify.android.model.remote.response

import com.google.gson.annotations.SerializedName

data class Edu(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("content")
    val content: String,

    @field:SerializedName("tags")
    val tags: List<String>
)