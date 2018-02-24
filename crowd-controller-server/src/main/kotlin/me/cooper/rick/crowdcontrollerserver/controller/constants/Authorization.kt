package me.cooper.rick.crowdcontrollerserver.controller.constants

class Authorization private constructor() {

    companion object {
        const val IS_ADMIN = "hasRole('ADMIN')"
        const val IS_USER = "isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username"
    }
}