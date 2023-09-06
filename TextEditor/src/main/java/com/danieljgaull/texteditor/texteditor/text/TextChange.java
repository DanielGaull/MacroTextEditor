package com.danieljgaull.texteditor.texteditor.text;

public class TextChange {

    private final String text;
    private final int caretPos;
    private final int linePos;
    private final boolean isNewLine;

    private TextChange(String text, int caretPos, int linePos, boolean isNewLine) {
        this.text = text;
        this.caretPos = caretPos;
        this.linePos = linePos;
        this.isNewLine = isNewLine;
    }

    public static TextChange newLine(int linePos) {
        return new TextChange(null, -1, linePos, true);
    }
    public static TextChange typeText(String text, int caretPos, int linePos) {
        return new TextChange(text, caretPos, linePos, false);
    }

    public String getText() {
        return text;
    }

    public int getCaretPos() {
        return caretPos;
    }

    public int getLinePos() {
        return linePos;
    }

    public boolean isNewLine() {
        return isNewLine;
    }
}
