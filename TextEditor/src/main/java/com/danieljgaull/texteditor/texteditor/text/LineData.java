package com.danieljgaull.texteditor.texteditor.text;

import java.util.HashMap;

public class LineData {

    private HashMap<String, DataValue> dataValues;

    public LineData() {
        dataValues = new HashMap<>();
    }

    public void setValue(String name, DataValue value) {
        dataValues.put(name, value);
    }

    public DataValue getValue(String name) {
        return dataValues.get(name);
    }

    // TODO: Read in/convert to a file (probably just have raw number/string/boolean)

}
