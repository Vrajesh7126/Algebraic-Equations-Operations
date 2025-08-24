package com.example.Exception;

/**
 * Exception thrown when an equation with the specified ID is not found.
 */
public class EquationNotFoundException extends RuntimeException {

    /**
     * Constructs a new EquationNotFoundException with a detailed message.
     *
     * @param equationId the ID of the equation that was not found
     */
    public EquationNotFoundException(int equationId) {
        super("Equation not found for ID: " + equationId);
    }
}
