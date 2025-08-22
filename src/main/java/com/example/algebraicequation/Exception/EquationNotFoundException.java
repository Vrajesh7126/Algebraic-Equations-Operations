package com.example.algebraicequation.Exception;

public class EquationNotFoundException extends RuntimeException {
    public EquationNotFoundException(int id) {
        super("Equation not found for ID: " + id);
    }
}
