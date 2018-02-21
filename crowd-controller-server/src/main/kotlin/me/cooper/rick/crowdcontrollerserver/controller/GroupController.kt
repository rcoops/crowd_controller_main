package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/groups")
class GroupController(private val groupService: GroupService) {

    @PostMapping
    fun create(@RequestBody userDto: UserDto) {
        groupService.create(userDto.id)
    }

}