package com.danieljgaull.texteditor.texteditor.expressions;

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
    private String variableName;

    // TODO: Add operator types (like the unary/binary/ternary operator type)

}
