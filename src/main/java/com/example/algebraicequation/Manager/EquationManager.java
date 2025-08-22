package com.example.algebraicequation.Manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.algebraicequation.Model.ExpressionNode;

@Component
public class EquationManager {
    private Map<Integer, ExpressionNode> equationStore = new HashMap<>();
    private int equationId = 1;

    public int addNewEquation(ExpressionNode equation) {
        equationStore.put(equationId, equation);
        return equationId++;
    }

    public Map<Integer, ExpressionNode> getAllEquations() {
        return equationStore;
    }

    public ExpressionNode getEquationById(int id) {
        return equationStore.get(id);
    }
}
