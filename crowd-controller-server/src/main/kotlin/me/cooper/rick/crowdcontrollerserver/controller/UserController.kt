package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping()
    fun users(): List<UserDto> = userService.allUsers()

    @GetMapping("/{username}")
    fun user(@PathVariable username: String): UserDto {
        return userService.user(username)
    }

    @PostMapping()
    fun create(@RequestBody dto: RegistrationDto): UserDto {
        return userService.create(dto)
    }

}