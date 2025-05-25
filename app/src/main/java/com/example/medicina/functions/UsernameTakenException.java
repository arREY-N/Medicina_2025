package com.example.medicina.functions; // Or com.example.medicina.exceptions;

public class UsernameTakenException extends Exception {
    public UsernameTakenException(String message) {
        super(message);
    }
    public UsernameTakenException() {
        super("This username is already taken.");
    }
}