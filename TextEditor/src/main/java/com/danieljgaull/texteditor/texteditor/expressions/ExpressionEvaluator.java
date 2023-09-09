package com.danieljgaull.texteditor.texteditor.expressions;

import com.danieljgaull.texteditor.texteditor.text.DataValue;
import com.danieljgaull.texteditor.texteditor.text.VariableData;

public class ExpressionEvaluator {

    // TODO: Exceptions
    public DataValue evaluate(Ast ast, VariableData data) {
        switch (ast.getType()) {
            case BooleanLiteral:
                return DataValue.bool(ast.getBoolValue());
            case StringLiteral:
                return DataValue.string(ast.getStringValue());
            case NumberLiteral:
                return DataValue.number(ast.getNumberValue());
            case FunctionCall:
                // TODO: Add functions
                return null;
            case Variable:
                return data.getValue(ast.getVarName());
            case UnaryOperation:
                DataValue v = evaluate(ast.getMiddle(), data);
                switch (ast.getUnaryOp()) {
                    case LogicalNegation:
                        if (v.getType() != DataTypes.Boolean) {
                            return null; // Exception
                        }
                        return DataValue.bool(!v.getBooleanValue());
                    case NumericNegation:
                        if (v.getType() != DataTypes.Number) {
                            return null; // Exception
                        }
                        return DataValue.number(-v.getNumberValue());
                }
                break;
            case BinaryOperation:
                DataValue left = evaluate(ast.getLeft(), data);
                DataValue right = evaluate(ast.getRight(), data);
                switch (ast.getBinaryOp()) {
                    case Addition:
                        // If two numbers, then add the number values
                        // Otherwise, convert to strings first
                        if (left.getType() == DataTypes.Number && right.getType() == DataTypes.Number) {
                            return DataValue.number(left.getNumberValue() + right.getNumberValue());
                        } else {
                            return DataValue.string(left.toString() + right.toString());
                        }
                    case Subtraction:
                        // Both types must be numbers
                        if (left.getType() == DataTypes.Number && right.getType() == DataTypes.Number) {
                            return DataValue.number(left.getNumberValue() - right.getNumberValue());
                        } else {
                            // TODO: Exception
                            return null;
                        }
                    case Multiplication:
                        // If both numbers, then multiply
                        // If second is number, then convert first to string and multiply
                        // Otherwise, exception
                        if (left.getType() == DataTypes.Number && right.getType() == DataTypes.Number) {
                            return DataValue.number(left.getNumberValue() * right.getNumberValue());
                        } else if (right.getType() == DataTypes.Number) {
                            StringBuilder result = new StringBuilder();
                            String repeatString = left.toString();
                            for (int i = 0; i < right.getNumberValue(); i++) {
                                result.append(repeatString);
                            }
                            return DataValue.string(result.toString());
                        } else {
                            // TODO: Exception
                            return null;
                        }
                    case Division:
                        // Both types must be numbers
                        if (left.getType() == DataTypes.Number && right.getType() == DataTypes.Number) {
                            return DataValue.number(left.getNumberValue() / right.getNumberValue());
                        } else {
                            // TODO: Exception
                            return null;
                        }
                    case LogicalAnd:
                        // If first is truthy, returns second
                        // Otherwise, returns first
                        boolean truthyValAnd = toTruthyFalsy(left);
                        if (truthyValAnd) {
                            return right;
                        }
                        return left;
                    case LogicalOr:
                        // If first is falsy, returns second
                        // Otherwise, returns first
                        boolean truthyValOr = toTruthyFalsy(left);
                        if (!truthyValOr) {
                            return right;
                        }
                        return left;
                    case IsEqual:
                        return DataValue.bool(left.isEqual(right));
                    case IsNotEqual:
                        return DataValue.bool(!left.isEqual(right));
                    case IsGreaterThan:
                        return DataValue.bool(left.isGreaterThan(right));
                    case IsLessThan:
                        return DataValue.bool(left.isLessThan(right));
                    case IsGreaterThanOrEqual:
                        return DataValue.bool(left.isGreaterThan(right) || left.isEqual(right));
                    case IsLessThanOrEqual:
                        return DataValue.bool(left.isLessThan(right) || left.isEqual(right));
                }
                break;
            case TernaryOperation:
                switch (ast.getTernaryOp()) {
                    case Conditional:
                        // Evaluate the condition
                        DataValue condition = evaluate(ast.getLeft(), data);
                        boolean truthyFalsy = toTruthyFalsy(condition);
                        if (!truthyFalsy) {
                            // Follow the falsy path
                            return evaluate(ast.getRight(), data);
                        }
                        // Follow the truthy path
                        return evaluate(ast.getMiddle(), data);
                }
                break;
        }
        return null;
    }

    // We define every value as truthy except the false value
    private boolean toTruthyFalsy(DataValue input) {
        if (input.getType() == DataTypes.Boolean && !input.getBooleanValue()) {
            return false;
        }
        return true;
    }

}
