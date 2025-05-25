package com.example.medicina.functions; // Or com.example.medicina.exceptions;

public class EmailTakenException extends Exception {
    public EmailTakenException(String message) {
        super(message);
    }
    public EmailTakenException() {
        super("This email address is already registered.");
    }
}