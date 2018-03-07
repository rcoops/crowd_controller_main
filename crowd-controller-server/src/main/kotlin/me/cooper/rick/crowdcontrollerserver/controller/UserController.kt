package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.LocationDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.constants.IS_ADMIN
import me.cooper.rick.crowdcontrollerserver.controller.constants.IS_PRINCIPAL
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
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun user(@PathVariable userId: Long, principal: Principal): UserDto? = userService.user(userId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(CREATED)
    @PreAuthorize("permitAll()")
    fun create(@RequestBody dto: RegistrationDto): UserDto = userService.create(dto)

    @PatchMapping("{userId}/location")
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun updateLocation(@PathVariable userId: Long,
                       @RequestBody dto: LocationDto, principal: Principal): UserDto {
        return userService.updateLocation(userId, dto)
    }

    @GetMapping("/{userId}/friends", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun friends(@PathVariable userId: Long, principal: Principal): List<FriendDto> = userService.friends(userId)

    @PostMapping("/{userId}/friends", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun addFriend(@PathVariable userId: Long,
                  @RequestBody friendDto: FriendDto, principal: Principal): List<FriendDto> {
        return userService.addFriend(userId, friendDto)
    }

    @PatchMapping("/{userId}/friends/{friendId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun updateFriendship(@PathVariable userId: Long,
                         @PathVariable friendId: Long,
                         @RequestBody friendDto: FriendDto, principal: Principal): List<FriendDto> {
        return userService.updateFriendship(userId, friendId, friendDto)
    }

    @DeleteMapping("/{userId}/friends/{friendId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("$IS_ADMIN or $IS_PRINCIPAL")
    fun deleteFriend(@PathVariable userId: Long,
                     @PathVariable friendId: Long, principal: Principal): List<FriendDto> {
        return userService.deleteFriend(userId, friendId)
    }

}
