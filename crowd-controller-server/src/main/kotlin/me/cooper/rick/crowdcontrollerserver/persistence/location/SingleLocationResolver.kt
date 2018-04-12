package me.cooper.rick.crowdcontrollerserver.persistence.location

import com.google.maps.model.LatLng
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group

internal class SingleLocationResolver : LocationResolver {

    override fun latLng(group: Group): LatLng? {
        return if (group.admin!!.hasLocation()) {
            LatLng(group.admin.latitude!!, group.admin.longitude!!)
        } else null
    }

}
