package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import me.cooper.rick.crowdcontrollerapi.dto.Message
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.service.SecurityService
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class UserController(private val userService: UserService,
                     private val securityService: SecurityService) {

    @GetMapping("/")
    fun home(): String {
        return "home"
    }

    @GetMapping("/add")
    @ResponseBody
    fun register(@RequestParam dto: UserDto): Message {
        val userDto = userService.save(dto)

        securityService.autoLogin(LoginDto(userDto.username, userDto.password))

        return Message(100, "YES!", "New user ${dto.username} created")
    }

    @GetMapping("/getmessage", produces = ["application/json"])
    @ResponseBody
    fun getMessage(): Message {
        return Message(100, "Congratulations!", "You have accessed a Basic Auth protected resource.")
    }

}