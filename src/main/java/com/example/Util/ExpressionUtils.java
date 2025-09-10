package com.example.Util;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import com.example.Model.ExpressionNode;

import java.util.regex.Matcher;

/**
 * Utility class for parsing, converting, building, and evaluating algebraic
 * expressions.
 * Supports infix-to-postfix conversion, expression tree construction,
 * and expression evaluation with variable substitution.
 */
public class ExpressionUtils {

    /**
     * Checks if the first operator has higher or equal precedence than the second.
     *
     * @param operator1 the operator on the stack
     * @param operator2 the current operator being processed
     * @return true if operator1 has higher or equal precedence, false otherwise
     */
    private static boolean higherPrecedenceValidate(char operator1, char operator2) {
        int precedence1 = getPrecedence(operator1);
        int precedence2 = getPrecedence(operator2);
        return precedence1 >= precedence2;
    }

    /**
     * Gets the precedence of a given operator.
     *
     * @param op the operator character
     * @return precedence level (higher number means higher precedence)
     */
    private static int getPrecedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Converts an infix expression into postfix (Reverse Polish Notation).
     * Supports multi-character operands (e.g., 10x, 2y).
     *
     * @param infix the infix expression string
     * @return the equivalent postfix expression string
     */
    public static String infixToPostFix(String infix) {
        Stack<Character> operators = new Stack<>();
        StringBuilder postfix = new StringBuilder();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            // Operand (letters, digits, underscores, decimal numbers)
            if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                StringBuilder operand = new StringBuilder();
                while (i < infix.length() &&
                        (Character.isLetterOrDigit(infix.charAt(i)) || infix.charAt(i) == '_'
                                || infix.charAt(i) == '.')) {
                    operand.append(infix.charAt(i));
                    i++;
                }
                i--;
                postfix.append(operand).append(" ");
            }
            // Operator
            else if ("+-*/^=".indexOf(c) >= 0) {
                while (!operators.isEmpty() && higherPrecedenceValidate(operators.peek(), c)) {
                    postfix.append(operators.pop()).append(" ");
                }
                operators.push(c);
            }
            // Opening parenthesis
            else if (c == '(') {
                operators.push(c);
            }
            // Closing parenthesis
            else if (c == ')') {
                boolean foundOpening = false;
                while (!operators.isEmpty()) {
                    char top = operators.pop();
                    if (top == '(') {
                        foundOpening = true;
                        break;
                    } else {
                        postfix.append(top).append(" ");
                    }
                }
                if (!foundOpening) {
                    return null; // unmatched '('
                }
            }
            // Invalid character
            else {
                return null;
            }
        }

        // Pop remaining operators
        while (!operators.isEmpty()) {
            char top = operators.pop();
            if (top == '(') {
                return null; // unmatched '('
            }
            postfix.append(top).append(" ");
        }

        return postfix.toString().trim();
    }

    /**
     * Builds an expression tree from a postfix expression.
     *
     * @param postfixTokens postfix expression string (tokens separated by space)
     * @return root node of the constructed expression tree
     */
    public static ExpressionNode buildTreeFromPostFix(String postfixTokens) {
        if (postfixTokens == null || postfixTokens.strip().isEmpty()) {
            return null; // invalid
        }

        Stack<ExpressionNode> stack = new Stack<>();
        String[] tokens = postfixTokens.strip().split("\\s+");
        boolean equalsUsed = false;

        for (String token : tokens) {
            // Operator
            if ("+-*/^=".contains(token)) {
                if ("=".equals(token)) {
                    if (equalsUsed) {
                        return null; // more than one '=' not allowed
                    }
                    equalsUsed = true;
                }

                // Ensure at least two operands
                if (stack.size() < 2) {
                    return null; // malformed expression
                }

                ExpressionNode right = stack.pop();
                ExpressionNode left = stack.pop();
                if (left == null || right == null)
                    return null;

                ExpressionNode parent = new ExpressionNode(token);
                parent.setLeft(left);
                parent.setRight(right);
                stack.push(parent);

            }
            // Operand
            else if (token.matches("[a-zA-Z0-9_]+")) {
                stack.push(new ExpressionNode(token));
            }
            // Any other character
            else {
                return null; // invalid token
            }
        }

        // Final check: exactly one node must remain
        if (stack.size() != 1) {
            return null; // malformed
        }

        return stack.pop();
    }

    /**
     * Converts an expression tree back into an infix expression.
     *
     * @param node the root node of the expression tree
     * @return the infix expression as a string
     */
    public static String buildInfixFromTree(ExpressionNode node) {
        if (node == null) {
            return "";
        }

        if (node.getLeft() == null && node.getRight() == null) {
            return node.getValue();
        }

        String left = buildInfixFromTree(node.getLeft());
        String right = buildInfixFromTree(node.getRight());

        return left + " " + node.getValue() + " " + right;
    }

    /**
     * Evaluates an expression tree with variable substitution.
     *
     * @param root      root node of the expression tree
     * @param variables map of variable names to their numeric values
     * @return evaluated result as a double
     */
    public static double evaluateExpressionTree(ExpressionNode root, Map<String, Double> variables) {
        if (root == null) {
            throw new IllegalArgumentException("Root cannot be null");
        }

        // Leaf node
        if (root.getLeft() == null && root.getRight() == null) {
            String value = root.getValue();

            // Numeric constant
            if (value.matches("-?\\d+(\\.\\d+)?")) {
                return Double.parseDouble(value);
            }

            // Coefficient-variable form (e.g., 3x, 2.5y, 2var_2)
            Pattern coefVarPattern = Pattern.compile("(-?\\d+(\\.\\d+)?)([a-zA-Z_][a-zA-Z0-9_]*)");
            Matcher matcher = coefVarPattern.matcher(value);
            if (matcher.matches()) {
                double coefficient = Double.parseDouble(matcher.group(1));
                String variable = matcher.group(3);

                if (!variables.containsKey(variable)) {
                    throw new IllegalArgumentException("Unknown variable: " + variable);
                }

                return coefficient * variables.get(variable);
            }

            // Standalone variable (with optional underscores)
            if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                if (!variables.containsKey(value)) {
                    throw new IllegalArgumentException("Unknown variable: " + value);
                }
                return variables.get(value);
            }

            throw new IllegalArgumentException("Unknown operand: " + value);
        }

        // Internal operator node
        double leftVal = evaluateExpressionTree(root.getLeft(), variables);
        double rightVal = evaluateExpressionTree(root.getRight(), variables);

        switch (root.getValue()) {
            case "+":
                return leftVal + rightVal;
            case "-":
                return leftVal - rightVal;
            case "*":
                return leftVal * rightVal;
            case "/":
                return leftVal / rightVal;
            case "^":
                return Math.pow(leftVal, rightVal);
            case "=":
                return leftVal - rightVal; // For equations
            default:
                throw new IllegalArgumentException("Unknown operator: " + root.getValue());
        }
    }
}
