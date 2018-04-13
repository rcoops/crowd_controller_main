package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class WebSocketController(private val simpMessagingTemplate: SimpMessagingTemplate) {

    fun send(groupDto: GroupDto) {
        logger.debug("sending group dto: $groupDto")
        simpMessagingTemplate.convertAndSend("/topic/group/${groupDto.id}", groupDto)
    }

    fun send(vararg userDtos: UserDto) {
        logger.debug("sending user dto(s): ${userDtos.map { it.toString() } }")
        userDtos.forEach { simpMessagingTemplate.convertAndSend("/topic/user/${it.id}", it) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

}
