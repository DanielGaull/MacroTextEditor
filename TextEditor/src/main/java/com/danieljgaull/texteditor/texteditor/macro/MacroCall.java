package com.danieljgaull.texteditor.texteditor.macro;

import com.danieljgaull.texteditor.texteditor.text.DataValue;

import java.util.List;

public record MacroCall(Macro macro, List<DataValue> args) {
}
