package com.example.Exception;

public class InvalidEquationException extends RuntimeException {
    public InvalidEquationException(String message) {
        super(message);
    }
}