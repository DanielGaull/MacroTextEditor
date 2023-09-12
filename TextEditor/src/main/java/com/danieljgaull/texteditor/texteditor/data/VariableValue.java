package com.danieljgaull.texteditor.texteditor.data;

public class VariableValue {

    private DataValue value;
    private Variable variable;

    public VariableValue(Variable variable, DataValue value) {
        this.variable = variable;
        this.value = value;
    }

    public void setValue(DataValue value) {
        this.value = value;
    }

    public DataValue getValue() {
        return value;
    }

    public Variable getVariable() {
        return variable;
    }

    public VariableValue clone() {
        return new VariableValue(variable.clone(), value.clone());
    }
}
