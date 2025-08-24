package com.example.Model;

/**
 * Represents a node in an algebraic expression tree.
 * Each node can be an operator (e.g., +, -, *, /) or an operand (e.g., variable
 * or number).
 */
public class ExpressionNode {

    // Value of the node: operator (+, -, *, /) or operand (e.g., x, y, 2)
    private String value;

    // Left child node in the expression tree
    private ExpressionNode left;

    // Right child node in the expression tree
    private ExpressionNode right;

    /**
     * Constructs an ExpressionNode with the specified value.
     *
     * @param value the value of the node (operator or operand)
     */
    public ExpressionNode(String value) {
        this.value = value;
    }

    /**
     * Gets the value of this node.
     *
     * @return the value (operator or operand)
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this node.
     *
     * @param value the value to set (operator or operand)
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the left child node.
     *
     * @return the left child node
     */
    public ExpressionNode getLeft() {
        return left;
    }

    /**
     * Sets the left child node.
     *
     * @param left the left child node to set
     */
    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    /**
     * Gets the right child node.
     *
     * @return the right child node
     */
    public ExpressionNode getRight() {
        return right;
    }

    /**
     * Sets the right child node.
     *
     * @param right the right child node to set
     */
    public void setRight(ExpressionNode right) {
        this.right = right;
    }
}
