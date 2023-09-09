package com.danieljgaull.texteditor.texteditor.text;

import com.danieljgaull.texteditor.texteditor.modes.Mode;

public class TextLine {

    private Mode lineMode;
    private String rawText;
    private VariableData lineData;

    public TextLine(Mode mode, String rawText, VariableData lineData) {
        this.lineMode = mode;
        this.rawText = rawText;
        this.lineData = lineData;
    }

    public String getRawText() {
        return rawText;
    }
    public Mode getLineMode() {
        return lineMode;
    }
    public VariableData getLineData() {
        return lineData;
    }
    public VariableData copyLineData() {
        return lineData.clone();
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

}
