package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.apache.tomcat.util.http.parser.Authorization
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun users(): List<UserDto> = userService.allUsers()

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun user(@PathVariable userId: Long, principal: Principal): UserDto? {
        return userService.user(userId)
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    fun create(@RequestBody dto: RegistrationDto): UserDto = userService.create(dto)

    @GetMapping("/{userId}/friends")
    @PreAuthorize("isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun friends(@PathVariable userId: Long,
                principal: Principal): Set<FriendDto> = userService.friends(userId)

    @PutMapping("/{userId}/friends/{friendIdentifier}")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun addFriend(@PathVariable userId: Long,
                  @PathVariable friendIdentifier: String,
                  principal: Principal): UserDto = userService.addFriend(userId, friendIdentifier)

    @PutMapping("/{userId}/friends/{friendId}/activate")
    @PreAuthorize("isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username")
    fun acceptFriendRequest(@PathVariable userId: Long,
                            @PathVariable friendId: Long,
                            principal: Principal): UserDto = userService.acceptFriendRequest(userId, friendId)

}