package com.danieljgaull.texteditor.texteditor.data;

import java.util.HashMap;
import java.util.Map;

public class VariableData {

    private HashMap<String, VariableValue> values;

    public VariableData() {
        this(new HashMap<>());
    }

    private VariableData(HashMap<String, VariableValue> values) {
        this.values = values;
    }

    public void setValue(String name, DataValue value) {
        if (values.containsKey(name)) {
            VariableValue var = values.get(name);
            if (var.getVariable().dataType() == value.getType()) {
                values.get(name).setValue(value);
            } else {
                throw new IllegalArgumentException("Cannot assign " + value.toString() + " to variable " +
                        var.getVariable().name() + " because it is of type " + var.getVariable().dataType().name());
            }
        } else {
            // Need to insert a new value into our hash map
            VariableValue var = new VariableValue(new Variable(name, value.getType()), value);
            values.put(name, var);
        }
    }

    public DataValue getValue(String name) {
        return values.get(name).getValue();
    }

    public VariableData clone() {
        HashMap<String, VariableValue> clonedMap = new HashMap<>();
        for (Map.Entry<String, VariableValue> entry : values.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return new VariableData(clonedMap);
    }

    // Will prioritize the concat'ed keys
    // ex if we have X = 5 and the other has X = 10,
    // the concat result will be X = 10
    public void concat(VariableData other) {
        for (Map.Entry<String, VariableValue> entry : other.values.entrySet()) {
            values.put(entry.getKey(), entry.getValue().clone());
        }
    }

    // TODO: Read in/convert to a file (probably just have raw number/string/boolean)

}
