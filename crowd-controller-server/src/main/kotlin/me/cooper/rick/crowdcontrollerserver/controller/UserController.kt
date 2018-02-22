package me.cooper.rick.crowdcontrollerserver.controller

import io.swagger.annotations.Api
import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
@Api(value = "User", description = "REST API for User", tags = ["User"])
class UserController(private val userService: UserService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun users(): List<UserDto> = userService.allUsers()

    @GetMapping("/{userId}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun user(@PathVariable userId: Long, principal: Principal): UserDto? = userService.user(userId)

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("permitAll()")
    fun create(@RequestBody dto: RegistrationDto): ResponseEntity<UserDto> {
        val userDto = userService.create(dto)
        return ResponseEntity(userDto, CREATED)
    }

    @GetMapping("/{userId}/friends", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun friends(@PathVariable userId: Long,
                principal: Principal): Set<FriendDto> = userService.friends(userId)

    @PutMapping("/{userId}/friends/{friendIdentifier}", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun addFriend(@PathVariable userId: Long,
                  @PathVariable friendIdentifier: String,
                  principal: Principal): UserDto = userService.addFriend(userId, friendIdentifier)

    @PutMapping("/{userId}/friends/{friendId}/activate", produces = [APPLICATION_JSON_VALUE])
    @PreAuthorize("isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun acceptFriendRequest(@PathVariable userId: Long,
                            @PathVariable friendId: Long,
                            principal: Principal): UserDto = userService.acceptFriendRequest(userId, friendId)

}