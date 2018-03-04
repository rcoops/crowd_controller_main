package me.cooper.rick.crowdcontrollerserver.controller.error.handler

import me.cooper.rick.crowdcontrollerapi.dto.error.APIErrorDto
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.ResourceNotFoundException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserGroupException
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handle(e: ResourceNotFoundException, request: WebRequest): ResponseEntity<Any> {
        return responseEntity(APIErrorDto(NOT_FOUND.value(), "Not Found", e.message))
    }

    @ExceptionHandler(UserGroupException::class)
    fun handle(e: UserGroupException, request: WebRequest): ResponseEntity<Any> {
        return responseEntity(APIErrorDto(BAD_REQUEST.value(), "Group Conflict", e.message))
    }

    @Throws(DataIntegrityViolationException::class)
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handle(e: DataIntegrityViolationException): ResponseEntity<Any> {
        fun isConstraintViolation(cause: Throwable?): Boolean {
            return cause != null && cause is ConstraintViolationException
        }

        val constraintName: String = if (isConstraintViolation(e.cause)) {
            (e.cause as ConstraintViolationException).constraintName
        } else throw e

        val message = CONSTRAINT_MESSAGE_MAP[constraintName]

        return if (message != null) {
            responseEntity(APIErrorDto(CONFLICT.value(), "User already exists!", message))
        } else throw e
    }

    private fun responseEntity(dto: APIErrorDto): ResponseEntity<Any> {
        return ResponseEntity(dto, HttpHeaders(), HttpStatus.valueOf(dto.status))
    }

    companion object {
        const val UNIQUE_MOBILE = "UK_mobile_number"
        const val UNIQUE_EMAIL = "UK_email"
        const val UNIQUE_USERNAME = "UK_username"
        private val CONSTRAINT_MESSAGE_MAP = mapOf(
                UNIQUE_MOBILE to "That mobile number already belongs to an existing user.",
                UNIQUE_EMAIL to "That email already belongs to an existing user.",
                UNIQUE_USERNAME to "That username already belongs to an existing user."
        )
    }

}
