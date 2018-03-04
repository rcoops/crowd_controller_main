package me.cooper.rick.crowdcontrollerserver.controller.constants

const val IS_ADMIN = "hasRole('ADMIN')"
const val IS_PRINCIPAL = "isAuthenticated() and #principal.name==@userServiceImpl.user(#userId)?.username"
