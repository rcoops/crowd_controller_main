package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerserver.controller.constants.Authorization.Companion.IS_ADMIN
import me.cooper.rick.crowdcontrollerserver.controller.constants.Authorization.Companion.IS_USER
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/groups")
@Api(value = "Group", description = "REST API for Group", tags = ["Group"])
class GroupController(private val groupService: GroupService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize(IS_ADMIN)
    fun groups(): List<GroupDto> = groupService.groups()

    @GetMapping("/{groupId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN")
    fun group(@PathVariable groupId: Long, principal: Principal): GroupDto? = groupService.group(groupId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(CREATED)
    @PreAuthorize("isAuthenticated()")
    fun create(@RequestBody createGroupDto: CreateGroupDto): GroupDto = groupService.create(createGroupDto)

    @PutMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN")
    fun addToGroup(@PathVariable groupId: Long,
                   @PathVariable userId: Long, principal: Principal): GroupDto {
        return groupService.addToGroup(groupId, userId)
    }

    @PutMapping("/{groupId}/members/{userId}/accept", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_USER")
    fun acceptGroupInvite(@PathVariable groupId: Long,
                          @PathVariable userId: Long, principal: Principal): GroupDto {
        return groupService.acceptGroupInvite(groupId, userId)
    }

    @DeleteMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN or $IS_USER")
    fun removeFromGroup(@PathVariable groupId: Long,
                        @PathVariable userId: Long, principal: Principal): GroupDto {
        return groupService.removeFromGroup(groupId, userId)
    }

    @DeleteMapping("/{groupId}", produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN")
    fun removeGroup(@PathVariable groupId: Long, principal: Principal) = groupService.removeGroup(groupId)

    companion object {
        const val IS_GROUP_ADMIN = "isAuthenticated() and #principal.name==@groupServiceImpl.admin(#groupId)"
    }

}