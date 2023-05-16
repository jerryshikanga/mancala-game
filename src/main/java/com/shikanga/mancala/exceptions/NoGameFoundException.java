package com.shikanga.mancala.exceptions;

public class NoGameFoundException extends RuntimeException{
    public NoGameFoundException(String message){
        super(message);
    }
}
