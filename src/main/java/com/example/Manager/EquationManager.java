package com.example.Manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.Model.ExpressionNode;

/**
 * Manages storage and retrieval of algebraic equations.
 * Provides methods to add, fetch all, and fetch by ID.
 */
@Component
public class EquationManager {

    // Stores equations with their unique IDs
    private final Map<Integer, ExpressionNode> equationStore = new HashMap<>();

    // Counter for generating unique equation IDs
    private int nextEquationId = 1;

    /**
     * Adds a new equation to the store.
     *
     * @param equation the ExpressionNode representing the equation
     * @return the unique ID assigned to the stored equation
     */
    public int addEquation(ExpressionNode equation) {
        equationStore.put(nextEquationId, equation);
        return nextEquationId++;
    }

    /**
     * Retrieves all stored equations.
     *
     * @return a map of equation IDs to ExpressionNode objects
     */
    public Map<Integer, ExpressionNode> getAllEquations() {
        return equationStore;
    }

    /**
     * Retrieves an equation by its ID.
     *
     * @param equationId the unique ID of the equation
     * @return the ExpressionNode for the given ID, or null if not found
     */
    public ExpressionNode getEquationById(int equationId) {
        return equationStore.get(equationId);
    }
}
