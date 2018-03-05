package me.cooper.rick.crowdcontrollerserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

/*
logs incoming requests
http://www.baeldung.com/spring-http-logging
*/
@Configuration
class RequestLoggingFilterConfig {

    @Bean
    fun logFilter(): CommonsRequestLoggingFilter {
        return CommonsRequestLoggingFilter().apply {
            setIncludeQueryString(true)
            setIncludeQueryString(true)
            setIncludePayload(true)
            setMaxPayloadLength(10000)
            isIncludeHeaders = false
            setAfterMessagePrefix("REQUEST DATA : ")
        }
    }

}
