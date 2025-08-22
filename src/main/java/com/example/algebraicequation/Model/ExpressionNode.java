package com.example.algebraicequation.Model;

public class ExpressionNode {
    private String value; // operator (+,-,*,/) or operand (x,y,2)
    private ExpressionNode left;
    private ExpressionNode right;

    public ExpressionNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public ExpressionNode getRight() {
        return right;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
    }

}
