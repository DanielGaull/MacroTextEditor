package com.danieljgaull.texteditor.texteditor.data;

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

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public DataValue clone() {
        return new DataValue(type, stringValue, numberValue, booleanValue);
    }

    public boolean isEqual(DataValue other) {
        if (other.getType() != type) {
            return false;
        }
        switch (type) {
            case String:
                return other.stringValue.equals(stringValue);
            case Number:
                return other.numberValue == numberValue;
            case Boolean:
                return other.booleanValue == booleanValue;
        }
        return false;
    }

    // Numbers - numerical order; strings - String.compare; booleans - true > false
    public boolean isGreaterThan(DataValue other) {
        if (other.getType() != type) {
            return false;
        }
        switch (type) {
            case String:
                return stringValue.compareTo(other.stringValue) > 0;
            case Number:
                return numberValue > other.numberValue;
            case Boolean:
                // Only greater than if this == true and other == false
                return booleanValue && !other.booleanValue;
        }
        return false;
    }

    public boolean isLessThan(DataValue other) {
        if (other.getType() != type) {
            return false;
        }
        switch (type) {
            case String:
                return stringValue.compareTo(other.stringValue) < 0;
            case Number:
                return numberValue < other.numberValue;
            case Boolean:
                // Only less than if this == false and other == true
                return !booleanValue && other.booleanValue;
        }
        return false;
    }

    public String toString() {
        switch (type) {
            case String:
                return stringValue;
            case Number:
                return Double.toString(numberValue);
            case Boolean:
                return Boolean.toString(booleanValue);
        }
        return "{UNK}";
    }
}
