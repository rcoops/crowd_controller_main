package me.cooper.rick.crowdcontrollerserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class CrowdControllerServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrowdControllerServerApplication::class.java, *args)
}
