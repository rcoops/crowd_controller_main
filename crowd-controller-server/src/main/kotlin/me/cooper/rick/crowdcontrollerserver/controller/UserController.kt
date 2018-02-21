package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun users(): List<UserDto> = userService.allUsers()

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    fun user(@PathVariable userId: Long): UserDto? = userService.user(userId)

    @PostMapping
    @PreAuthorize("permitAll()")
    fun create(@RequestBody dto: RegistrationDto): UserDto = userService.create(dto)

    @GetMapping("/{userId}/friends")
    @PreAuthorize("isAuthenticated()")
    fun friends(@PathVariable userId: Long): Set<FriendDto> = userService.friends(userId)

    @PutMapping("/{userId}/friends/{friendIdentifier}")
    @PreAuthorize("isAuthenticated()")
    fun addFriend(@PathVariable userId: Long,
                  @PathVariable friendIdentifier: String): UserDto = userService.addFriend(userId, friendIdentifier)

    @PutMapping("/{userId}/friends/{friendId}/activate")
    @PreAuthorize("isAuthenticated()")
    fun acceptFriendRequest(@PathVariable userId: Long,
                  @PathVariable friendId: Long): UserDto = userService.acceptFriendRequest(userId, friendId)

}