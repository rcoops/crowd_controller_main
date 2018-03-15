package me.cooper.rick.crowdcontrollerapi.dto.error

import com.fasterxml.jackson.annotation.JsonProperty

// Json properties allow me to map this to spring response errors
class APIErrorDto(@get:JsonProperty("status")
                  val status: Int = DEFAULT_STATUS,

                  @get:JsonProperty("error")
                  val error: String = DEFAULT_ERROR,

                  @get:JsonProperty("error_description")
                  val errorDescription: String = DEFAULT_DESCRIPTION) {

    companion object {

        private const val DEFAULT_STATUS = 503
        const val DEFAULT_ERROR = "Connection Error"
        const val DEFAULT_DESCRIPTION = "Unfortunately the service is not currently available. Please try again later."

    }

}
