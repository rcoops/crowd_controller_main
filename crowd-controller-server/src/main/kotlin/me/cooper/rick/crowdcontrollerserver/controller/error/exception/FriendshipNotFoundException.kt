package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class FriendshipNotFoundException(username: String, friendUsername: String)
    : ResourceNotFoundException("No friendship exists between $username and $friendUsername")