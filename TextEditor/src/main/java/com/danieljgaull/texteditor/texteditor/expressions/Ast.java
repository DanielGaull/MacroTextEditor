package com.danieljgaull.texteditor.texteditor.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Ast {

    private AstTypes type;

    // Used in binary/ternary operations
    private Ast left;
    private Ast right;
    // Used in unary/ternary operations
    private Ast middle;

    // For literal/variable types
    private String stringValue;
    private double numberValue;
    private boolean boolValue;

    // Used for variable names and function calls
    private String variableName;
    // Currently only used for function args
    private List<Ast> children;

    // Operator types
    private int op;

    private Ast(AstTypes type, Ast left, Ast right, Ast middle, String str, double num, boolean bool, String var,
                int op, List<Ast> children) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.middle = middle;
        this.stringValue = str;
        this.numberValue = num;
        this.boolValue = bool;
        this.variableName = var;
        this.op = op;
        this.children = children;
    }

    public static Ast string(String value) {
        return new Ast(AstTypes.StringLiteral, null, null, null, value, 0, false, null, -1, new ArrayList<>());
    }
    public static Ast number(double value) {
        return new Ast(AstTypes.NumberLiteral, null, null, null, null, value, false, null, -1, new ArrayList<>());
    }
    public static Ast bool(boolean value) {
        return new Ast(AstTypes.BooleanLiteral, null, null, null, null, 0, value, null, -1, new ArrayList<>());
    }
    public static Ast variable(String name) {
        return new Ast(AstTypes.Variable, null, null, null, null, 0, false, name, -1, new ArrayList<>());
    }
    public static Ast functionCall(String name, List<Ast> args) {
        return new Ast(AstTypes.FunctionCall, null, null, null, null, 0, false, name, -1, args);
    }
    public static Ast unary(Ast single, UnaryOperators operator) {
        return new Ast(AstTypes.UnaryOperation, null, null, single, null, 0, false, null,
                operator.ordinal(), new ArrayList<>());
    }
    public static Ast binary(Ast left, Ast right, BinaryOperators operator) {
        return new Ast(AstTypes.BinaryOperation, left, right, null, null, 0, false, null,
                operator.ordinal(), new ArrayList<>());
    }
    public static Ast ternary(Ast left, Ast middle, Ast right, TernaryOperators operator) {
        return new Ast(AstTypes.TernaryOperation, left, right, middle, null, 0, false, null,
                operator.ordinal(), new ArrayList<>());
    }

    public AstTypes getType() {
        return type;
    }

    public Ast getLeft() {
        return left;
    }
    public Ast getRight() {
        return right;
    }
    public Ast getMiddle() {
        return middle;
    }

    public String getStringValue() {
        return stringValue;
    }
    public double getNumberValue() {
        return numberValue;
    }
    public boolean getBoolValue() {
        return boolValue;
    }

    public String getVarName() {
        return variableName;
    }
    public List<Ast> getChildren() {
        return children;
    }

    public BinaryOperators getBinaryOp() {
        return BinaryOperators.values()[op];
    }
    public UnaryOperators getUnaryOp() {
        return UnaryOperators.values()[op];
    }
    public TernaryOperators getTernaryOp() {
        return TernaryOperators.values()[op];
    }

}
