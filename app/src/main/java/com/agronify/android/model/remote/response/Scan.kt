package com.agronify.android.model.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
    @field:SerializedName("path")
    val path: String
)

@Parcelize
data class PredictResponse(
    @field:SerializedName("path")
    val path: String,

    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("disease")
    val disease: DiseaseDetail? = null,

    @field:SerializedName("confidence")
    val accuracy: Float
) : Parcelable

@Parcelize
data class DiseaseDetail(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String
) : Parcelable