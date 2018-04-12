package me.cooper.rick.crowdcontrollerserver.persistence.location

import com.google.maps.model.LatLng
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group

internal interface LocationResolver {

    fun latLng(group: Group): LatLng?

}
