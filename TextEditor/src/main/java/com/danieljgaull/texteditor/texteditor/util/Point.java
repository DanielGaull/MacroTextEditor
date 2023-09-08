package com.danieljgaull.texteditor.texteditor.util;

public class Point {
    public double x;
    public double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Point(double i) {
        this(i, i);
    }
    public Point() {
        this(0);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
