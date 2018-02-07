package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import me.cooper.rick.crowdcontrollerapi.dto.Message
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.SecurityService
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService,
                     private val securityService: SecurityService) {

//    @GetMapping("/")
//    fun home(): String {
//        return "home"
//    }
//
//    @PostMapping("/register", consumes = ["application/json"], produces = ["application/json"])
//    @ResponseBody
//    fun register(@RequestParam dto: UserDto): Message {
//        val userDto = userService.save(dto)
//
//        securityService.autoLogin(LoginDto(userDto.username, userDto.password))
//
//        return Message(100, "YES!", "New user ${dto.username} created")
//    }
//
//    @GetMapping("/getmessage", produces = ["application/json"])
//    @ResponseBody
//    fun getMessage(): Message {
//        return Message(100, "Congratulations!", "You have accessed a Basic Auth protected resource.")
//    }

    @GetMapping("/new")
    fun makeUser(@RequestParam username: String, password: String) {
        val dto = UserDto(username, password)
        userService.save(dto)
    }

    @GetMapping("/users")
    fun users(): List<UserDto> = userService.allUsers()

}