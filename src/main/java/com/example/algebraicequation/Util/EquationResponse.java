package com.example.algebraicequation.Util;

public class EquationResponse {
    private String equationId;
    private String equation;

    public EquationResponse(String equationId, String equation) {
        this.equationId = equationId;
        this.equation = equation;
    }

    public String getEquationId() {
        return equationId;
    }

    public String getEquation() {
        return equation;
    }
}
