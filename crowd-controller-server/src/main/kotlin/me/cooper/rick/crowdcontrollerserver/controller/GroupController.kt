package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/groups")
@Api(value = "Group", description = "REST API for Group", tags = ["Group"])
class GroupController(private val groupService: GroupService) {

    @GetMapping
    fun groups(): List<GroupDto> = groupService.groups()

    @GetMapping("/{groupId}")
    fun group(@PathVariable groupId: Long): GroupDto? = groupService.group(groupId)

    @PostMapping
    fun create(@RequestBody userDto: UserDto): UserDto = groupService.create(userDto.id)

    @PutMapping("/{groupId}/members/{userId}")
    fun addToGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long): GroupDto = groupService.addToGroup(groupId, userId)

    @DeleteMapping("/{groupId}/members/{userId}")
    fun removeFromGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long): GroupDto = groupService.removeFromGroup(groupId, userId)

    @DeleteMapping("/{groupId}")
    fun removeGroup(@PathVariable groupId: Long): Boolean = groupService.removeGroup(groupId)

}