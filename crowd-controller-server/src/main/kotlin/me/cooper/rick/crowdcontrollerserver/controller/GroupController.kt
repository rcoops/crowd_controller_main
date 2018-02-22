package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
@Api(value = "Group", description = "REST API for Group", tags = ["Group"])
class GroupController(private val groupService: GroupService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun groups(): List<GroupDto> = groupService.groups()

    @GetMapping("/{groupId}", produces = [APPLICATION_JSON_VALUE])
    fun group(@PathVariable groupId: Long): GroupDto? = groupService.group(groupId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody groupDto: GroupDto): ResponseEntity<UserDto> {
        val group = groupService.create(groupDto)
        return ResponseEntity(group, CREATED)
    }

    @PutMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    fun addToGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long): GroupDto = groupService.addToGroup(groupId, userId)

    @DeleteMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    fun removeFromGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long): GroupDto = groupService.removeFromGroup(groupId, userId)

    @DeleteMapping("/{groupId}", produces = [APPLICATION_JSON_VALUE])
    fun removeGroup(@PathVariable groupId: Long): Boolean = groupService.removeGroup(groupId)

}