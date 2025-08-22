package com.example.algebraicequation.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.algebraicequation.Exception.EquationNotFoundException;
import com.example.algebraicequation.Manager.EquationManager;
import com.example.algebraicequation.Model.ExpressionNode;
import com.example.algebraicequation.Util.Operation;

@Service
public class EquationService {
    int elemendId = 1;
    private final EquationManager equationManager;

    EquationService(EquationManager equationManager) {
        this.equationManager = equationManager;
    }

    public Map<String, Object> storeEquation(String infixExpression) {
        // Step 1: Convert infix to postfix tokens
        List<String> postfixTokens = Operation.infixToPostfix(infixExpression);

        // Step 2: Build ExpressionNode tree from postfix tokens
        ExpressionNode eqRootNode = Operation.buildTreeFromPostfix(postfixTokens);

        // Step 3: Store the equation and get ID
        int equationId = equationManager.addNewEquation(eqRootNode);

        // Step 4: Prepare response
        Map<String, Object> equationMap = new HashMap<>();
        equationMap.put("message", "Equation stored successfully");
        equationMap.put("equationId", equationId);

        return equationMap;
    }

    public Map<String, Object> getAllEquations() {
        Map<Integer, ExpressionNode> equations = equationManager.getAllEquations();

        List<Map<String, String>> responseList = new ArrayList<>();

        for (Map.Entry<Integer, ExpressionNode> entry : equations.entrySet()) {
            int equationId = entry.getKey();
            ExpressionNode eqRootNode = entry.getValue();

            // Convert tree to infix
            String infix = Operation.buildInfixFromTree(eqRootNode);

            // Optional: remove outer parentheses
            if (infix.startsWith("(") && infix.endsWith(")")) {
                infix = infix.substring(1, infix.length() - 1);
            }

            Map<String, String> equationMap = new HashMap<>();
            equationMap.put("equationId", String.valueOf(equationId));
            equationMap.put("equation", infix);
            responseList.add(equationMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("equations", responseList);

        return response;
    }

    public Map<String, Object> evaluateEquation(int equationId, Map<String, Double> variables) {
        ExpressionNode root = equationManager.getEquationById(equationId);
        if (root == null) {
            throw new EquationNotFoundException(equationId);
        }

        double result = evaluateExpressionNode(root, variables);

        String infix = Operation.buildInfixFromTree(root);
        if (infix.startsWith("(") && infix.endsWith(")")) {
            infix = infix.substring(1, infix.length() - 1);
        }

        return Map.of(
                "equationId", String.valueOf(equationId),
                "equation", infix,
                "variables", variables,
                "result", result);
    }

    private double evaluateExpressionNode(ExpressionNode node, Map<String, Double> variables) {
        if (node == null)
            return 0;

        String val = node.getValue();

        if (Operation.isOperator(val)) {
            double leftVal = evaluateExpressionNode(node.getLeft(), variables);
            double rightVal = evaluateExpressionNode(node.getRight(), variables);
            return Operation.applyOperator(leftVal, rightVal, val);
        }

        // Leaf node: number or variable
        try {
            return Double.parseDouble(val); // numeric literal
        } catch (NumberFormatException e) {
            if (variables.containsKey(val)) {
                return variables.get(val); // variable value from request
            } else {
                // If it’s like "3x", you should split digit and variable
                if (val.matches("\\d+[a-zA-Z]+")) {
                    int i = 0;
                    while (i < val.length() && Character.isDigit(val.charAt(i)))
                        i++;
                    double coeff = Double.parseDouble(val.substring(0, i));
                    String variable = val.substring(i);
                    if (variables.containsKey(variable)) {
                        return coeff * variables.get(variable);
                    }
                }
                throw new RuntimeException("Unknown variable: " + val);
            }
        }
    }
}
