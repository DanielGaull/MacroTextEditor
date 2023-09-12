package com.danieljgaull.texteditor.texteditor.macro;

import com.danieljgaull.texteditor.texteditor.data.DataTypes;

public class MacroParameter {
    public String name;
    public DataTypes type;

    public MacroParameter(String name, DataTypes type) {
        this.name = name;
        this.type = type;
    }
}
