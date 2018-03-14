package me.cooper.rick.crowdcontrollerserver.persistence.location

import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group

internal class SingleLocationResolver : LocationResolver {

    override fun location(group: Group): LocationDto {
        return LocationDto(group.id, group.admin?.latitude, group.admin?.longitude)
    }

}
