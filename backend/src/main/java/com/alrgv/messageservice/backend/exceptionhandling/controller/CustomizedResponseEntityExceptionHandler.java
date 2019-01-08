package com.alrgv.messageservice.backend.exceptionhandling.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/*@ControllerAdvice
@RestController*/
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

/*    @ExceptionHandler(Exception.class)*/
    public final String handleUserNotFoundException(Exception ex, WebRequest request) {
        return "Oops!" + ex.getMessage();
    }
}
