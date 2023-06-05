package com.agronify.android.model.remote.response

import com.google.gson.annotations.SerializedName

data class Scan(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("type")
    val type: String,

    @field:SerializedName("is_fruit")
    val isFruit: Boolean
)

data class UploadResponse(
    @field:SerializedName("url")
    val url: String
)