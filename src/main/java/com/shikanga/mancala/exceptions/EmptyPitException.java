package com.shikanga.mancala.exceptions;

public class EmptyPitException extends RuntimeException{
    public EmptyPitException(String message){
        super(message);
    }
}
