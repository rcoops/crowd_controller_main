package me.cooper.rick.crowdcontrollerserver.job

import me.cooper.rick.crowdcontrollerserver.service.GroupService
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
internal class GroupExpiryChecker(private val groupService: GroupService,
                                  private val userService: UserService) {

    @Scheduled(fixedRate = 60000) // Once a minute
    fun checkGroups() = groupService.expireGroups()

    // this must be done with a reasonable delay to ensure all clients have stopped sending location,
    // otherwise the operation will need to be carried out more than once
    @Scheduled(fixedRate = 60000, initialDelay = 10000) // Once a minute, delay of 10 seconds
    fun clearLocation() = userService.clearLocationOfUngroupedUsers()

}
