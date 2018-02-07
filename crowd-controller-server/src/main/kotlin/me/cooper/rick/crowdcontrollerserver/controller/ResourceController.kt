package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import me.cooper.rick.crowdcontrollerapi.dto.Message
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.City
import me.cooper.rick.crowdcontrollerserver.repository.CityRepository
import me.cooper.rick.crowdcontrollerserver.service.SecurityService
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/springjwt")
class ResourceController(
        private val cityRepository: CityRepository,
        private val userService: UserService,
        private val securityService: SecurityService
) {


    @PostMapping("/register", consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    fun register(@RequestParam dto: UserDto): Message {
        val userDto = userService.save(dto)

        securityService.autoLogin(LoginDto(userDto.username, userDto.password))

        return Message(100, "YES!", "New user ${dto.username} created")
    }

    @GetMapping("/cities")
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
    fun cities(): List<City> = cityRepository.findAll()
}