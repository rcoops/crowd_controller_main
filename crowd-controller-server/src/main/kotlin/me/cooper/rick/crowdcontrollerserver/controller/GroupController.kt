package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.group.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerserver.controller.constants.IS_ADMIN
import me.cooper.rick.crowdcontrollerserver.controller.constants.IS_PRINCIPAL
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
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_MEMBER")
    fun group(@PathVariable groupId: Long, principal: Principal): GroupDto? = groupService.group(groupId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(CREATED)
    @PreAuthorize("isAuthenticated()")
    fun create(@RequestBody createGroupDto: CreateGroupDto): GroupDto = groupService.create(createGroupDto)

    @PutMapping("/{groupId}")
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN")
    fun update(@PathVariable groupId: Long,
               @RequestBody groupDto: GroupDto, principal: Principal): GroupDto = groupService.update(groupId, groupDto)

    @PatchMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun acceptInvite(@PathVariable groupId: Long,
                     @PathVariable userId: Long, principal: Principal): GroupDto {
        return groupService.acceptInvite(groupId, userId)
    }

    @DeleteMapping("/{groupId}/members/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN or $IS_PRINCIPAL")
    fun removeFromGroup(@PathVariable groupId: Long,
                        @PathVariable userId: Long, principal: Principal): GroupDto? {
        return groupService.removeFromGroup(groupId, userId)
    }

    @DeleteMapping("/{groupId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("$IS_ADMIN or $IS_GROUP_ADMIN")
    fun removeGroup(@PathVariable groupId: Long, principal: Principal) = groupService.removeGroup(groupId)

    companion object {
        const val IS_GROUP_ADMIN = "isAuthenticated() and #principal.name==@groupServiceImpl.admin(#groupId)"
        const val IS_GROUP_MEMBER = "isAuthenticated() and @groupServiceImpl.isInGroup(#groupId, #principal.name)"
    }

}
