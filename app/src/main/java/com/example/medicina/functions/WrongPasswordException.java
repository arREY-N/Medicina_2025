package com.example.medicina.functions; // Or com.example.medicina.exceptions;

public class WrongPasswordException extends Exception {
    public WrongPasswordException(String message) {
        super(message);
    }
    public WrongPasswordException() {
        super("Incorrect password for the provided username.");
    }
}