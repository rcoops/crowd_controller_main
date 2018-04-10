package me.cooper.rick.crowdcontrollerserver.service

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.GeocodingApi.reverseGeocode
import com.google.maps.model.LatLng
import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerserver.persistence.location.LocationResolver
import me.cooper.rick.crowdcontrollerserver.persistence.location.MultiLocationResolver
import me.cooper.rick.crowdcontrollerserver.persistence.location.SingleLocationResolver
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.GroupSettings
import org.springframework.stereotype.Service

@Service
internal class LocationResolverServiceImpl(private val geoApiContext: GeoApiContext) : LocationResolverService {

    private val singleLocationResolver: SingleLocationResolver = SingleLocationResolver()

    override fun resolveLocation(group: Group): LocationDto {
        val resolver = resolver(group.settings)
        val latLng = resolver.latLng(group)
        return LocationDto(group.id, latLng.lat, latLng.lng, getAddress(latLng))
    }

    private fun resolver(settings: GroupSettings): LocationResolver {
        return if (settings.isClustering) {
            MultiLocationResolver(settings.minNodePercentage, settings.minClusterRadius)
        } else {
            singleLocationResolver
        }
    }

    private fun getAddress(latLng: LatLng): String? {
        val results = reverseGeocode(geoApiContext, latLng).await()

        return results[0].formattedAddress
    }

}
