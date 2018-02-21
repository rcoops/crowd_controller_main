package me.cooper.rick.crowdcontrollerapi.dto

import com.google.gson.annotations.SerializedName

data class Token(@SerializedName("access_token") val accessToken: String? = null,
                 @SerializedName("token_type") val tokenType: String = "bearer",
                 @SerializedName("expires_in") val expiresIn: Int = -1,
                 val scope: String = "read",
                 val jti: String? = null,
                 val user: UserDto? = null)