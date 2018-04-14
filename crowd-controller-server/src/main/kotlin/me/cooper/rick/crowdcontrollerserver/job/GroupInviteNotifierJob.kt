package me.cooper.rick.crowdcontrollerserver.job

import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GroupInviteNotifierJob(private val userService: UserService) {

    @Scheduled(fixedRate = 5000)
    fun sendGroupInvites() = userService.sendGroupInvites()

}
