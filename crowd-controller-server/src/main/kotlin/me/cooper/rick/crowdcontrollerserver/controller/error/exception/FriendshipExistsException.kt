package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class FriendshipExistsException(username: String): ResourceExistsException("You are already friends with $username")
