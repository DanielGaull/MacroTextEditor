package com.danieljgaull.texteditor.texteditor.macro;

import com.danieljgaull.texteditor.texteditor.data.DataValue;

import java.util.List;

public record MacroCall(Macro macro, List<DataValue> args) {
}
