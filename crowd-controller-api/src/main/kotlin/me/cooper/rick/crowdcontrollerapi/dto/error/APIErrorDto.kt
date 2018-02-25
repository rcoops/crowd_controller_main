package me.cooper.rick.crowdcontrollerapi.dto.error

import com.google.gson.annotations.SerializedName

class APIErrorDto(val status: Int = DEFAULT_STATUS,
                  val error: String = DEFAULT_ERROR,
                  @SerializedName("error_description")
                  val errorDescription: String = DEFAULT_DESCRIPTION) {

    companion object {

        private const val DEFAULT_STATUS = 503
        private const val DEFAULT_ERROR = "Connection Error"
        private const val DEFAULT_DESCRIPTION = "Unfortunately the service is not currently available. Please try again later."

    }

}
