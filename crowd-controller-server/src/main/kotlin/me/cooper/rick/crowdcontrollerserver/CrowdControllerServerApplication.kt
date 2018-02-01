package me.cooper.rick.crowdcontrollerserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class CrowdControllerServerApplication


fun main(args: Array<String>) {
    SpringApplication.run(CrowdControllerServerApplication::class.java, *args)
}
