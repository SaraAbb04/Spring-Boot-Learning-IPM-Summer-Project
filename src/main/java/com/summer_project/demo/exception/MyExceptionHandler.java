package com.summer_project.demo.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException exception){
        return new ResponseEntity<>(
                generate(exception),
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPasswordException(InvalidPasswordException exception){
        return new ResponseEntity<>(
          generate(exception),
          HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException exception){
        return new ResponseEntity<>(
                generate(exception),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmailException(EmailAlreadyExistsException exception){
        return new ResponseEntity<>(
                generate(exception),
                HttpStatus.ALREADY_REPORTED
        );
    }
    private Map<String, String> generate(Throwable throwable){
        var response = new HashMap<String, String>();
        response.put("time", new Date().toString());
        response.put("message", throwable.getMessage());
        return response;
    }
}
