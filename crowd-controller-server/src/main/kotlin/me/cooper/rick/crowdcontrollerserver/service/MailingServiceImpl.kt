package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerserver.controller.UserController
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.internet.InternetAddress

@Service
internal class MailingServiceImpl(private val mailSender: JavaMailSender,
                                  @Value("\${my.application.url}") private val host: String) : MailingService {

    override fun sendPasswordResetMail(user: User, uuid: String): Boolean {
        return sendResetEmail(user.email, generateResetRequestBody(user, uuid))
    }

    private fun sendResetEmail(email: String, body: String): Boolean {
        val preparator = MimeMessagePreparator { mimeMessage ->
            mimeMessage.apply {
                setRecipients(Message.RecipientType.TO, arrayOf(InternetAddress(email)))
                sender = InternetAddress(NO_REPLY_EMAIL)
                setFrom(InternetAddress(NO_REPLY_EMAIL))
                subject = "Password Reset Request"
                setText(body)
            }
        }
        return try {
            mailSender.send(preparator)
            true
        } catch (e: MailException) {
            LOGGER.error("Password reset email sending failed to $email!", e)
            false
        }
    }

    override fun sendNewPasswordMail(user: User, newPassword: String) {
        sendResetEmail(user.email, generateResetPasswordBody(user.username, newPassword))
    }

    private fun generateResetRequestBody(user: User, uuid: String): String {
        val resetLink = "$host/users${UserController.PASSWORD_RESET_PATH}?email=${user.email}&token=$uuid"
        return "Dear ${user.username},\nYou are receiving this message because a password reset has been requested" +
                " for your account. Please follow this link to reset.\n\t$resetLink" +
                "\nKind Regards,\n\tThe Crowd Controller Team"
    }

    private fun generateResetPasswordBody(username: String, newPassword: String): String {
        return "Dear $username,\nYour new password is:\n\t$newPassword\nWe advise you to change it at " +
                "the earliest convenience.\nKind Regards,\n\tThe Crowd Controller Team"
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailingServiceImpl::class.java)
        private const val NO_REPLY_EMAIL = "noreply@crowdcontroller.com"
    }

}
