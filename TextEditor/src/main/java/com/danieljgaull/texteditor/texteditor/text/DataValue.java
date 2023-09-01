package com.danieljgaull.texteditor.texteditor.text;

import com.danieljgaull.texteditor.texteditor.expressions.DataTypes;

public class DataValue {

    private DataTypes type;
    private String stringValue;
    private double numberValue;
    private boolean booleanValue;

    private DataValue(DataTypes type, String stringValue, double numberValue, boolean booleanValue) {
        this.type = type;
        this.stringValue = stringValue;
        this.numberValue = numberValue;
        this.booleanValue = booleanValue;
    }

    public static DataValue string(String value) {
        return new DataValue(DataTypes.String, value, 0, false);
    }
    public static DataValue number(double value) {
        return new DataValue(DataTypes.Number, null, value, false);
    }
    public static DataValue bool(boolean value) {
        return new DataValue(DataTypes.Boolean, null, 0, value);
    }

    public DataTypes getType() {
        return type;
    }

    public String getStringValue() {
        return stringValue;
    }

    public double getNumberValue() {
        return numberValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }
}
