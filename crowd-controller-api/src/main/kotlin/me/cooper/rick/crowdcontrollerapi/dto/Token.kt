package me.cooper.rick.crowdcontrollerapi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Token(
        @param:JsonProperty("access_token")
        @get:JsonProperty("access_token")
        val accessToken: String? = null,

        @param:JsonProperty("token_type")
        @get:JsonProperty("token_type")
        val tokenType: String = "bearer",

        @param:JsonProperty("expires_in")
        @get:JsonProperty("expires_in")
        val expiresIn: Int = -1,

        val scope: String = "read",

        val jti: String? = null,

        val user: UserDto? = null
)