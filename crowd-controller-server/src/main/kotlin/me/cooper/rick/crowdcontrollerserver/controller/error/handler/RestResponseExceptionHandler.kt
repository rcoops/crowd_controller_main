package me.cooper.rick.crowdcontrollerserver.controller.error.handler

import me.cooper.rick.crowdcontrollerapi.dto.error.APIErrorDto
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.ResourceNotFoundException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserGroupException
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
        return responseEntity(APIErrorDto(NOT_FOUND.value(), e.message))
    }

    @ExceptionHandler(value = [UserGroupException::class])
    fun handle(e: UserGroupException, request: WebRequest): ResponseEntity<Any> {
        return responseEntity(APIErrorDto(BAD_REQUEST.value(), e.message))
    }

    private fun responseEntity(dto: APIErrorDto): ResponseEntity<Any> {
        return ResponseEntity(dto, HttpHeaders(), HttpStatus.valueOf(dto.status))
    }

}