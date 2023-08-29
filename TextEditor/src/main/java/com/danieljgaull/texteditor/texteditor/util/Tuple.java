package com.danieljgaull.texteditor.texteditor.util;

public class Tuple<T, S> {

    private T first;
    private S second;

    public Tuple(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public S second() {
        return second;
    }
}
