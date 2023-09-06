package com.danieljgaull.texteditor.texteditor.text;

import java.util.HashMap;
import java.util.Map;

public class LineData {

    private HashMap<String, DataValue> dataValues;

    public LineData() {
        this(new HashMap<>());
    }

    private LineData(HashMap<String, DataValue> dataValues) {
        this.dataValues = dataValues;
    }

    public void setValue(String name, DataValue value) {
        dataValues.put(name, value);
    }

    public DataValue getValue(String name) {
        return dataValues.get(name);
    }

    public LineData clone() {
        HashMap<String, DataValue> clonedMap = new HashMap<>();
        for (Map.Entry<String, DataValue> entry : dataValues.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return new LineData(clonedMap);
    }

    // TODO: Read in/convert to a file (probably just have raw number/string/boolean)

}
