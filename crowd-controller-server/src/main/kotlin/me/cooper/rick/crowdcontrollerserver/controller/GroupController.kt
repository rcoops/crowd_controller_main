package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/groups")
class GroupController(private val groupService: GroupService) {

    @PostMapping
    fun create(@RequestBody userDto: UserDto): UserDto = groupService.create(userDto.id)

    @PutMapping("/{groupId}/members/{userId}")
    fun addToGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long): GroupDto = groupService.addToGroup(groupId, userId)

    @DeleteMapping("/{groupId}/members/{userId}")
    fun removeFromGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long): GroupDto = groupService.removeFromGroup(groupId, userId)

}