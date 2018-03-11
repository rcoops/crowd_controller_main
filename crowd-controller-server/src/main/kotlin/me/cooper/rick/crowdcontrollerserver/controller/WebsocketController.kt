package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class WebsocketController(private val simpMessagingTemplate: SimpMessagingTemplate) {

    fun greeting(groupDto: GroupDto) {
        simpMessagingTemplate.convertAndSend("/topic/greetings", groupDto)
    }

}
