package com.danieljgaull.texteditor.texteditor.text;

import java.util.HashMap;
import java.util.Map;

public class VariableData {

    private HashMap<String, DataValue> dataValues;

    public VariableData() {
        this(new HashMap<>());
    }

    private VariableData(HashMap<String, DataValue> dataValues) {
        this.dataValues = dataValues;
    }

    public void setValue(String name, DataValue value) {
        dataValues.put(name, value);
    }

    public DataValue getValue(String name) {
        return dataValues.get(name);
    }

    public VariableData clone() {
        HashMap<String, DataValue> clonedMap = new HashMap<>();
        for (Map.Entry<String, DataValue> entry : dataValues.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return new VariableData(clonedMap);
    }

    // Will prioritize the concat'ed keys
    // ex if we have X = 5 and the other has X = 10,
    // the concat result will be X = 10
    public void concat(VariableData other) {
        for (Map.Entry<String, DataValue> entry : other.dataValues.entrySet()) {
            dataValues.put(entry.getKey(), entry.getValue().clone());
        }
    }

    // TODO: Read in/convert to a file (probably just have raw number/string/boolean)

}
