package me.cooper.rick.crowdcontrollerapi.dto.group

data class LocationDto(val id: Long? = null,
                       val latitude: Double? = null,
                       val longitude: Double? = null) {

    fun hasLocation() = latitude != null && longitude != null

}
