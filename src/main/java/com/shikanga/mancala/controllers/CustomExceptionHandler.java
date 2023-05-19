package com.shikanga.mancala.controllers;

import com.shikanga.mancala.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import org.springframework.data.redis.RedisConnectionFailureException;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);
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

    @ExceptionHandler(GameOverException.class)
    public ErrorResponse handleGameOverException(GameOverException ex){
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build();
    }

    @ExceptionHandler(JedisConnectionException.class)
    public ErrorResponse handleJedisConnectionException(JedisConnectionException ex){
        logger.error("Failed to connect to redis : "+ex.getMessage() + " : ", ex);
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to database.").build();
    }

    @ExceptionHandler(JedisDataException.class)
    public ErrorResponse handleRedisWriteException(JedisDataException ex){
        logger.error("Failed to write to redis : "+ex.getMessage(), ex);
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error writing to database.").build();
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ErrorResponse handleRedisConnectionFailureException(RedisConnectionFailureException ex){
        logger.error("Failed to connect to redis : "+ex.getMessage() + " : ", ex);
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to database.").build();
    }
}
