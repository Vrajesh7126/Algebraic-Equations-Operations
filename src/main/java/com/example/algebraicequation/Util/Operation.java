package com.example.algebraicequation.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.example.algebraicequation.Model.ExpressionNode;

public class Operation {

    // ================= Public Methods =================

    /**
     * Convert an infix expression string to ExpressionNode tree
     * Example: "3x+2y-z" -> ExpressionNode tree
     */
    public static ExpressionNode buildTreeFromInfix(String infixExpression) {
        return new Parser(infixExpression).parse();
    }

    /**
     * Convert an ExpressionNode tree back to infix string
     * Example: ExpressionNode tree of "3x+2y-z" -> "3x + 2y - z"
     */
    public static String buildInfixFromTree(ExpressionNode node) {
        if (node == null)
            return "";

        // Leaf node (operand)
        if (node.getLeft() == null && node.getRight() == null) {
            return node.getValue();
        }

        // Recursively convert left and right subtrees
        String left = buildInfixFromTree(node.getLeft());
        String right = buildInfixFromTree(node.getRight());

        // Add parentheses around every operation
        return "(" + left + " " + node.getValue() + " " + right + ")";
    }

    /**
     * Check if a token is an operator
     */
    public static boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    /**
     * Apply an operator to two numeric operands
     */
    public static double applyOperator(double left, double right, String operator) {
        return switch (operator) {
            case "+" -> left + right;
            case "-" -> left - right;
            case "*" -> left * right;
            case "/" -> left / right;
            case "^" -> Math.pow(left, right);
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    // ================= Private Internal Parser =================

    private static class Parser {
        private final String input;
        private int pos = 0;

        public Parser(String input) {
            this.input = input.replaceAll("\\s+", ""); // remove spaces
        }

        public ExpressionNode parse() {
            ExpressionNode node = parseExpression();
            if (pos < input.length()) {
                throw new RuntimeException("Unexpected character at position " + pos + ": " + input.charAt(pos));
            }
            return node;
        }

        // + and -
        private ExpressionNode parseExpression() {
            ExpressionNode left = parseTerm();
            while (pos < input.length()) {
                char c = input.charAt(pos);
                if (c == '+' || c == '-') {
                    pos++;
                    ExpressionNode right = parseTerm();
                    ExpressionNode parent = new ExpressionNode(String.valueOf(c));
                    parent.setLeft(left);
                    parent.setRight(right);
                    left = parent;
                } else {
                    break;
                }
            }
            return left;
        }

        // * and /
        private ExpressionNode parseTerm() {
            ExpressionNode left = parseFactor();
            while (pos < input.length()) {
                char c = input.charAt(pos);
                if (c == '*' || c == '/') {
                    pos++;
                    ExpressionNode right = parseFactor();
                    ExpressionNode parent = new ExpressionNode(String.valueOf(c));
                    parent.setLeft(left);
                    parent.setRight(right);
                    left = parent;
                } else {
                    break;
                }
            }
            return left;
        }

        // ^ (exponent)
        private ExpressionNode parseFactor() {
            ExpressionNode left = parseOperand();
            while (pos < input.length() && input.charAt(pos) == '^') {
                pos++;
                ExpressionNode right = parseOperand();
                ExpressionNode parent = new ExpressionNode("^");
                parent.setLeft(left);
                parent.setRight(right);
                left = parent;
            }
            return left;
        }

        private ExpressionNode parseOperand() {
            if (pos >= input.length())
                throw new RuntimeException("Unexpected end of input");

            char c = input.charAt(pos);

            if (c == '(') {
                pos++;
                ExpressionNode node = parseExpression();
                if (pos >= input.length() || input.charAt(pos) != ')') {
                    throw new RuntimeException("Missing closing parenthesis at position " + pos);
                }
                pos++;
                return node;
            } else {
                StringBuilder sb = new StringBuilder();
                boolean hasDigit = false;
                boolean hasLetter = false;

                while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
                    char ch = input.charAt(pos++);
                    if (Character.isDigit(ch))
                        hasDigit = true;
                    if (Character.isLetter(ch))
                        hasLetter = true;
                    sb.append(ch);
                }

                String token = sb.toString();

                // Case: coefficient + variable, e.g., "3x"
                if (hasDigit && hasLetter) {
                    int i = 0;
                    while (i < token.length() && Character.isDigit(token.charAt(i)))
                        i++;
                    String numberPart = token.substring(0, i);
                    String variablePart = token.substring(i);

                    ExpressionNode numberNode = new ExpressionNode(numberPart);
                    ExpressionNode variableNode = new ExpressionNode(variablePart);

                    ExpressionNode multiplyNode = new ExpressionNode("*");
                    multiplyNode.setLeft(numberNode);
                    multiplyNode.setRight(variableNode);
                    return multiplyNode;
                }

                return new ExpressionNode(token);
            }
        }
    }

    // Convert infix to postfix tokens
    public static List<String> infixToPostfix(String infixExpression) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        StringBuilder token = new StringBuilder();
        for (int i = 0; i < infixExpression.length(); i++) {
            char c = infixExpression.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                token.append(c);
            } else {
                if (token.length() > 0) {
                    output.add(token.toString());
                    token.setLength(0);
                }

                if (c == '(') {
                    stack.push("(");
                } else if (c == ')') {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        output.add(stack.pop());
                    }
                    stack.pop(); // remove '('
                } else if ("+-*/^".indexOf(c) >= 0) {
                    while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(String.valueOf(c))) {
                        output.add(stack.pop());
                    }
                    stack.push(String.valueOf(c));
                }
            }
        }

        if (token.length() > 0) {
            output.add(token.toString());
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private static int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    // Build ExpressionNode tree from postfix tokens
    public static ExpressionNode buildTreeFromPostfix(List<String> postfixTokens) {
        Stack<ExpressionNode> stack = new Stack<>();

        for (String token : postfixTokens) {
            if ("+-*/^".contains(token)) {
                ExpressionNode right = stack.pop();
                ExpressionNode left = stack.pop();
                ExpressionNode parent = new ExpressionNode(token);
                parent.setLeft(left);
                parent.setRight(right);
                stack.push(parent);
            } else {
                stack.push(new ExpressionNode(token));
            }
        }

        return stack.pop();
    }
}
