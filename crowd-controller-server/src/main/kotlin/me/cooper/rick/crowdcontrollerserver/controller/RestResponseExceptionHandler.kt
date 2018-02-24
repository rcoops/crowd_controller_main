package me.cooper.rick.crowdcontrollerserver.controller

import me.cooper.rick.crowdcontrollerserver.controller.exception.APIError
import me.cooper.rick.crowdcontrollerserver.controller.exception.ResourceNotFoundException
import me.cooper.rick.crowdcontrollerserver.controller.exception.UserGroupException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseExceptionHandler: ResponseEntityExceptionHandler() {
    
    @ExceptionHandler(value = [ResourceNotFoundException::class])
    fun handle(e: ResourceNotFoundException, request: WebRequest): ResponseEntity<Any> {
        return responseEntity(APIError(NOT_FOUND.value(), e.message))
    }

    @ExceptionHandler(value = [UserGroupException::class])
    fun handle(e: UserGroupException, request: WebRequest): ResponseEntity<Any> {
        return responseEntity(APIError(BAD_REQUEST.value(), e.message))
    }

    private fun responseEntity(apiError: APIError): ResponseEntity<Any> {
        return ResponseEntity(apiError, HttpHeaders(), HttpStatus.valueOf(apiError.status))
    }

}