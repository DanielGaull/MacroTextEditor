package com.danieljgaull.texteditor.texteditor.util;

public class Truple<T, S, R> {
    private T first;
    private S second;
    private R third;

    public Truple(T first, S second, R third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T first() {
        return first;
    }
    public S second() {
        return second;
    }
    public R third() {
        return third;
    }
}
