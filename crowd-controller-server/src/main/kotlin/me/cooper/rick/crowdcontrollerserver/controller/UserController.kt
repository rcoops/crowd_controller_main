package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.constants.Authorization.Companion.IS_ADMIN
import me.cooper.rick.crowdcontrollerserver.controller.constants.Authorization.Companion.IS_USER
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
@Api(value = "User", description = "REST API for User", tags = ["User"])
class UserController(private val userService: UserService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize(IS_ADMIN)
    fun users(): List<UserDto> = userService.allUsers()

    @GetMapping("/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_USER")
    fun user(@PathVariable userId: Long, principal: Principal): UserDto? = userService.user(userId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(CREATED)
    @PreAuthorize("permitAll()")
    fun create(@RequestBody dto: RegistrationDto): UserDto = userService.create(dto)

    @GetMapping("/{userId}/friends", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_USER")
    fun friends(@PathVariable userId: Long, principal: Principal): Set<FriendDto> = userService.friends(userId)

    @PutMapping("/{userId}/friends/{friendIdentifier:.*}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_USER")
    fun addFriend(@PathVariable userId: Long,
                  @PathVariable friendIdentifier: String, principal: Principal): Set<FriendDto> {
        return userService.addFriend(userId, friendIdentifier)
    }

    @PutMapping("/{userId}/friends/{friendId}/accept", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_USER")
    fun acceptFriendRequest(@PathVariable userId: Long,
                            @PathVariable friendId: Long, principal: Principal): Set<FriendDto> {
        return userService.acceptFriendRequest(userId, friendId)
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @PreAuthorize("$IS_ADMIN or $IS_USER")
    fun deleteFriend(@PathVariable userId: Long,
                     @PathVariable friendId: Long, principal: Principal): Set<FriendDto> {
        return userService.deleteFriend(userId, friendId)
    }

}
