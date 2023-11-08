package com.factorit.challenge.exception;

public class AlreadyExistsException extends IllegalArgumentException{
    public AlreadyExistsException(String message) {
        super(message);
    }
}
