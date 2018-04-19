package me.cooper.rick.crowdcontrollerapi.dto.group

import java.time.LocalDateTime

data class LocationDto(val id: Long? = null,
                       val latitude: Double? = null,
                       val longitude: Double? = null,
                       val address: String? = null,
                       val lastUpdate: LocalDateTime? = null) {

    fun hasLocation() = latitude != null && longitude != null

}
