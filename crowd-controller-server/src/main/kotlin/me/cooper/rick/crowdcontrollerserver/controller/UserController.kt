package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    fun users(): List<UserDto> = userService.allUsers()

    @GetMapping("/{username}")
    @PreAuthorize("isAuthenticated()")
    fun user(@PathVariable username: String): UserDto = userService.user(username)

    @PostMapping()
    @PreAuthorize("permitAll()")
    fun create(@RequestBody dto: RegistrationDto): UserDto = userService.create(dto)

}