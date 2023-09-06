package com.danieljgaull.texteditor.texteditor.text;

import com.danieljgaull.texteditor.texteditor.modes.Mode;

public class TextLine {

    private Mode lineMode;
    private String rawText;
    private LineData lineData;

    public TextLine(Mode mode, String rawText, LineData lineData) {
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
    public LineData copyLineData() {
        return lineData;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

}
