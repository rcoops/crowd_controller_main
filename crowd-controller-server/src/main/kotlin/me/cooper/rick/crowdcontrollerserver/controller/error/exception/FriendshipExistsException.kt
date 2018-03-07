package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class FriendshipExistsException(username: String): BadHttpRequestException("You are already friends with $username")
