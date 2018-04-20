package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerserver.persistence.model.User

internal interface MailingService {

    fun sendPasswordResetMail(user: User, uuid: String): Boolean

    fun sendNewPasswordMail(user: User, newPassword: String)

}
