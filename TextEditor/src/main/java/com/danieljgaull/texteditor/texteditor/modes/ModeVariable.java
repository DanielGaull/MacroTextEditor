package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.data.DataValue;
import com.danieljgaull.texteditor.texteditor.data.Variable;

public record ModeVariable(Variable var, DataValue defaultValue) {

}
