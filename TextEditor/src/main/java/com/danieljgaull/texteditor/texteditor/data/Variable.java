package com.danieljgaull.texteditor.texteditor.data;

public record Variable(String name, DataTypes dataType) {

    public Variable clone() {
        return new Variable(name, dataType);
    }

}
