package com.example.medicina.functions; // Or com.example.medicina.exceptions;

public class NoAccountException extends Exception {
    public NoAccountException(String message) {
        super(message);
    }
    public NoAccountException() {
        super("No account found with the provided username.");
    }
}