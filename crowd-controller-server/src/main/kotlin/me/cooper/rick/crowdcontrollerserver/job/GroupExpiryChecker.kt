package me.cooper.rick.crowdcontrollerserver.job

import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
internal class GroupExpiryChecker(private val groupService: GroupService) {

    @Scheduled(fixedRate = 60000) // Once a minute
    fun checkGroups() = groupService.expireGroups()

}
