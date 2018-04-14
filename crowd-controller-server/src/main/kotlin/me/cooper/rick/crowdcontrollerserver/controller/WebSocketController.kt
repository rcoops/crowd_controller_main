package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.error.APIErrorDto
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

    fun sendGroupExpiredNotification(groupDto: GroupDto) {
        logger.debug("sending expiry for group: $groupDto")
        val message = "Your group has expired as it has gone over its max life of ${groupDto.settings!!.maxLifeInHours} hours"
        simpMessagingTemplate.convertAndSend(
                "/topic/group/${groupDto.id}",
                APIErrorDto(200, "Group Expired", message)
        )
    }

    fun send(vararg userDtos: UserDto) {
        if (userDtos.isNotEmpty()) logger.debug("sending user dto(s): ${userDtos.map(UserDto::toString)}")
        userDtos.forEach { simpMessagingTemplate.convertAndSend("/topic/user/${it.id}", it) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

}
