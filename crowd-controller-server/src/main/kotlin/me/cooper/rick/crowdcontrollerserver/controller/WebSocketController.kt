package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class WebSocketController(private val simpMessagingTemplate: SimpMessagingTemplate) {

    fun send(groupDto: GroupDto) {
        simpMessagingTemplate.convertAndSend("/topic/group/${groupDto.id}", groupDto)
    }

    fun send(vararg userDtos: UserDto) {
        userDtos.forEach { simpMessagingTemplate.convertAndSend("/topic/user/${it.id}", it) }
    }

}
