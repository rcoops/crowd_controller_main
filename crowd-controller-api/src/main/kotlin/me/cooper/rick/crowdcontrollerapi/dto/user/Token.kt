package me.cooper.rick.crowdcontrollerapi.dto.user

import com.fasterxml.jackson.annotation.JsonProperty
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto

data class Token(
        @get:JsonProperty("access_token")
        val accessToken: String? = null,

        @get:JsonProperty("token_type")
        val tokenType: String = "bearer",

        @get:JsonProperty("expires_in")
        val expiresIn: Int = -1,

        @get:JsonProperty("scope")
        val scope: String = "read",

        @get:JsonProperty("jti")
        val jti: String? = null,

        @get:JsonProperty("user")
        val user: UserDto? = null
)
