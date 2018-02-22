package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/groups")
@Api(value = "Group", description = "REST API for Group", tags = ["Group"])
class GroupController(private val groupService: GroupService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun groups(): List<GroupDto> = groupService.groups()

    @GetMapping("/{groupId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@groupServiceImpl.admin(#groupId)")
    fun group(@PathVariable groupId: Long, principal: Principal): GroupDto? = groupService.group(groupId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("permitAll()")
    fun create(@RequestBody createGroupDto: CreateGroupDto): ResponseEntity<GroupDto> {
        val group = groupService.create(createGroupDto)
        return ResponseEntity(group, CREATED)
    }

    @PutMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@groupServiceImpl.admin(#groupId)")
    fun addToGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long, principal: Principal): GroupDto {
        return groupService.addToGroup(groupId, userId)
    }

    @DeleteMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@groupServiceImpl.admin(#groupId) or isAuthenticated() and @userServiceImpl.user(#userId)?.username")
    fun removeFromGroup(@PathVariable groupId: Long,
                        @PathVariable userId: Long, principal: Principal): GroupDto {
        return groupService.removeFromGroup(groupId, userId)
    }

    @DeleteMapping("/{groupId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@groupServiceImpl.admin(#groupId)")
    fun removeGroup(@PathVariable groupId: Long, principal: Principal): Boolean = groupService.removeGroup(groupId)

}