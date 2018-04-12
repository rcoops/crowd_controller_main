package me.cooper.rick.crowdcontrollerserver.config

import com.google.maps.GeoApiContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleApiConfig {

    @Bean
    fun geoApiContext(): GeoApiContext {
        return GeoApiContext.Builder()
                .apiKey("AIzaSyAUDQrN7RZNw02YJtqomJgH_zlbNaqCYEY")
                .build()
    }

}
