package com.danieljgaull.texteditor.texteditor.text;

public class TextChange {

    private final String text;
    private final boolean isNewLine;

    private TextChange(String text, boolean isNewLine) {
        this.text = text;
        this.isNewLine = isNewLine;
    }

    public static TextChange newLine() {
        return new TextChange(null, true);
    }
    public static TextChange typeText(String text) {
        return new TextChange(text, false);
    }

    public String getText() {
        return text;
    }

    public boolean isNewLine() {
        return isNewLine;
    }
}
