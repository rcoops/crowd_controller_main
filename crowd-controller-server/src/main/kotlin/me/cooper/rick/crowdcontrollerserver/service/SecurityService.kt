package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto

interface SecurityService {

    fun findLoggedInUsername(): String?

    fun autoLogin(dto: LoginDto)

}