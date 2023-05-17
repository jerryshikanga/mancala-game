package com.shikanga.mancala.controllers;

import com.shikanga.mancala.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(InvalidPitException.class)
    public ErrorResponse handleInvalidPitException(InvalidPitException ex){
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    }

    @ExceptionHandler(EmptyPitException.class)
    public ErrorResponse handleEmptyPitException(EmptyPitException ex){
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    }

    @ExceptionHandler(InvalidPlayerException.class)
    public ErrorResponse handleInvalidPlayerException(InvalidPlayerException ex){
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    }

    @ExceptionHandler(NoGameFoundException.class)
    public ErrorResponse handleNoGameFoundException(NoGameFoundException ex){
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage()).build();
    }

    @ExceptionHandler(WrongPlayerException.class)
    public ErrorResponse handleWrongPlayerException(WrongPlayerException ex){
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    }
}
