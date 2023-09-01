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

}
