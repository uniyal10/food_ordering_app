package com.upgrad.FoodOrderingApp.api.exception;


import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CustomerExceptionHandler {


    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictions(SignUpRestrictedException exe, WebRequest webRequest) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }



    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authFailedException(AuthenticationFailedException s, WebRequest webRequest) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(s.getCode()).message(s.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }


}