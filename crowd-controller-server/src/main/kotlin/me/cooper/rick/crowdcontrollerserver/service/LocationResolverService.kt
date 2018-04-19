package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group

internal interface LocationResolverService {

    fun resolveLocation(group: Group): LocationDto?

}
