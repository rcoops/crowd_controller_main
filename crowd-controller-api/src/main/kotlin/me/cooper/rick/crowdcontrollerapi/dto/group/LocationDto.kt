package me.cooper.rick.crowdcontrollerapi.dto.group

data class LocationDto(val id: Long? = null,
                       val latitude: Double? = null,
                       val longitude: Double? = null,
                       val address: String? = null,
                       val lastUpdate: String? = null) {

    fun hasLocation() = latitude != null && longitude != null

}
