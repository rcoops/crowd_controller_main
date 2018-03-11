package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class WebsocketController(private val simpMessagingTemplate: SimpMessagingTemplate) {

    fun send(groupDto: GroupDto) {
        simpMessagingTemplate.convertAndSend("/topic/greetings/${groupDto.id}", groupDto)
    }

    fun send(vararg userDtos: UserDto) {
        userDtos.forEach {
            simpMessagingTemplate.convertAndSend("/topic/user/${it.id}", it)
        }
    }

}
