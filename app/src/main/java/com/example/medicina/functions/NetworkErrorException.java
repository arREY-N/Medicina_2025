package com.example.medicina.functions; // Or com.example.medicina.exceptions;

public class NetworkErrorException extends Exception {
    public NetworkErrorException(String message, Throwable cause) {
        super(message, cause);
    }
    public NetworkErrorException(String message) {
        super(message);
    }
}