package com.example.DTO;

import java.util.Map;

/**
 * DTO for representing the response of an equation evaluation.
 * Contains the equation ID, the equation as a string, the variables used, and
 * the result.
 */
public class EquationEvaluationResponse {

    // Unique identifier for the equation
    private int equationId;

    // The equation in infix string format
    private String equation;

    // Map of variable names to their values used in evaluation
    private Map<String, Double> variables;

    // The result of the equation evaluation
    private double result;

    /**
     * Default constructor.
     */
    public EquationEvaluationResponse() {
    }

    /**
     * Constructs an EquationEvaluationResponse with all fields.
     *
     * @param equationId the unique ID of the equation
     * @param equation   the equation as a string
     * @param variables  the variables used in evaluation
     * @param result     the result of the evaluation
     */
    public EquationEvaluationResponse(int equationId, String equation,
            Map<String, Double> variables, double result) {
        this.equationId = equationId;
        this.equation = equation;
        this.variables = variables;
        this.result = result;
    }

    /**
     * Gets the equation ID.
     *
     * @return the equation ID
     */
    public int getEquationId() {
        return equationId;
    }

    /**
     * Sets the equation ID.
     *
     * @param equationId the equation ID to set
     */
    public void setEquationId(int equationId) {
        this.equationId = equationId;
    }

    /**
     * Gets the equation string.
     *
     * @return the equation string
     */
    public String getEquation() {
        return equation;
    }

    /**
     * Sets the equation string.
     *
     * @param equation the equation string to set
     */
    public void setEquation(String equation) {
        this.equation = equation;
    }

    /**
     * Gets the variables used in evaluation.
     *
     * @return a map of variable names to their values
     */
    public Map<String, Double> getVariables() {
        return variables;
    }

    /**
     * Sets the variables used in evaluation.
     *
     * @param variables a map of variable names to their values
     */
    public void setVariables(Map<String, Double> variables) {
        this.variables = variables;
    }

    /**
     * Gets the result of the evaluation.
     *
     * @return the result value
     */
    public double getResult() {
        return result;
    }

    /**
     * Sets the result of the evaluation.
     *
     * @param result the result value to set
     */
    public void setResult(double result) {
        this.result = result;
    }

}