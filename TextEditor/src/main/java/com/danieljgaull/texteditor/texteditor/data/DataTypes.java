package com.danieljgaull.texteditor.texteditor.data;

public enum DataTypes {
    String,
    Number,
    Boolean;

    public static DataTypes parse(String input) {
        switch (input) {
            case "string":
                return String;
            case "number":
                return Number;
            case "boolean":
                return Boolean;
        }
        throw new IllegalArgumentException("String is invalid data type");
    }
}
