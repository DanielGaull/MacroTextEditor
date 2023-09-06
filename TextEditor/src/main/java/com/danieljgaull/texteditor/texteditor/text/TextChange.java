package com.danieljgaull.texteditor.texteditor.text;

public class TextChange {

    private final String text;
    private final boolean isNewLine;
    private final boolean isDelete;

    private TextChange(String text, boolean isNewLine, boolean isDelete) {
        this.text = text;
        this.isNewLine = isNewLine;
        this.isDelete = isDelete;
    }

    public static TextChange newLine() {
        return new TextChange(null, true, false);
    }
    public static TextChange typeText(String text) {
        return new TextChange(text, false, false);
    }

    public String getText() {
        return text;
    }

    public boolean isNewLine() {
        return isNewLine;
    }

    public boolean isDelete() {
        return isDelete;
    }
}
