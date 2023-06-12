package com.agronify.android.model.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable