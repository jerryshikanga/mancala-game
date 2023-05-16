package com.shikanga.mancala.exceptions;

public class WrongPlayerException extends RuntimeException{
    public WrongPlayerException(String message){
        super(message);
    }
}
