package com.agronify.android.model.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("token")
    val token: String
)

data class Auth(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String
)