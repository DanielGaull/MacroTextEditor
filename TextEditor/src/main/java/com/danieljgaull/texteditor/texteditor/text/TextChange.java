package com.danieljgaull.texteditor.texteditor.text;

public class TextChange {

    private String text;
    private boolean isNewLine;
    private boolean isDelete;
    private int rangeStart;
    private int rangeEnd;

    public TextChange() {
        text = "";
        isNewLine = false;
        isDelete = false;
        rangeStart = rangeEnd = 0;
    }

    public TextChange delete(int start, int end) {
        rangeStart = start;
        rangeEnd = end;
        isDelete = true;
        return this;
    }
    public TextChange type(String text) {
        this.text = text;
        return this;
    }
    public TextChange newLine() {
        isNewLine = true;
        return this;
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

    public int getRangeStart() {
        return rangeStart;
    }
    public int getRangeEnd() {
        return rangeEnd;
    }
}
