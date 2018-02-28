package me.cooper.rick.crowdcontrollerapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Token(@JsonProperty("access_token") val accessToken: String? = null,
                 @JsonProperty("token_type") val tokenType: String = "bearer",
                 @JsonProperty("expires_in") val expiresIn: Int = -1,
                 val scope: String = "read",
                 val jti: String? = null,
                 val user: UserDto? = null)