package me.cooper.rick.crowdcontrollerserver.job

import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GroupInviteNotifierJob(private val userService: UserService,
                             private val webSocketController: WebSocketController) {

    @Scheduled(fixedRate = 5000)
    fun sendGroupInvites() {
        val users: List<UserDto> = userService.findAllWithPendingInvites()

        webSocketController.send(*users.toTypedArray())
    }

}
