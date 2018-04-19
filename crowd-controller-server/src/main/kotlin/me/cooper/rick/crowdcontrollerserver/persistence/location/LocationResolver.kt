package me.cooper.rick.crowdcontrollerserver.persistence.location

import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group

internal interface LocationResolver {

    fun location(group: Group): LocationDto?

}
