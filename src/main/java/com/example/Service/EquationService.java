package com.example.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.DTO.EquationEvaluationResponse;
import com.example.Exception.EquationNotFoundException;
import com.example.Exception.EvaluationException;
import com.example.Exception.InvalidEquationException;
import com.example.Manager.EquationManager;
import com.example.Model.ExpressionNode;
import com.example.Util.ExpressionUtils;

/**
 * Service class for managing algebraic equations.
 * Handles storing, retrieving, and evaluating equations.
 */
@Service
public class EquationService {

    private final EquationManager equationManager;

    /**
     * Constructor for dependency injection of EquationManager.
     *
     * @param equationManager the manager responsible for equation storage and
     *                        retrieval
     */
    public EquationService(EquationManager equationManager) {
        this.equationManager = equationManager;
    }

    /**
     * Stores a new equation by parsing the infix expression and building its tree.
     *
     * @param infixExpression the infix algebraic expression as a string
     * @return the unique ID assigned to the stored equation
     */
    public int storeEquation(String infixExpression) {
        try {
            String postfixExpression = ExpressionUtils.infixToPostFix(infixExpression);
            ExpressionNode equationRootNode = ExpressionUtils.buildTreeFromPostFix(postfixExpression);

            if (equationRootNode == null) {
                throw new InvalidEquationException("Invalid equation syntax: " + infixExpression);
            }

            return equationManager.addEquation(equationRootNode);
        } catch (Exception e) {
            throw new InvalidEquationException("Invalid equation: " + infixExpression);
        }
    }

    /**
     * Retrieves all stored equations as a list of maps containing equation ID and
     * infix string.
     *
     * @return a list of maps with equation details
     */
    public List<Map<String, String>> getAllEquations() {
        Map<Integer, ExpressionNode> equations = equationManager.getAllEquations();
        List<Map<String, String>> responseList = new ArrayList<>();

        for (Map.Entry<Integer, ExpressionNode> entry : equations.entrySet()) {
            int equationId = entry.getKey();
            ExpressionNode equationRootNode = entry.getValue();

            // Convert tree to infix string
            String infix = ExpressionUtils.buildInfixFromTree(equationRootNode);

            Map<String, String> equationMap = new HashMap<>();
            equationMap.put("equationId", String.valueOf(equationId));
            equationMap.put("equation", infix);
            responseList.add(equationMap);
        }

        return responseList;
    }

    /**
     * Evaluates a specific equation using provided variable values.
     *
     * @param equationId the ID of the equation to evaluate
     * @param variables  a map of variable names to their values
     * @return an EquationEvaluationResponse containing evaluation details
     */
    public EquationEvaluationResponse evaluateEquation(int equationId, Map<String, Double> variables) {
        ExpressionNode equationRootNode = equationManager.getEquationById(equationId);

        if (equationRootNode == null) {
            throw new EquationNotFoundException(equationId);
        }

        try {
            String infixExpression = ExpressionUtils.buildInfixFromTree(equationRootNode);
            double result = ExpressionUtils.evaluateExpressionTree(equationRootNode, variables);

            return new EquationEvaluationResponse(equationId, infixExpression, variables, result);
        } catch (Exception e) {
            throw new EvaluationException("Failed to evaluate equation: " + e.getMessage());
        }
    }
}
