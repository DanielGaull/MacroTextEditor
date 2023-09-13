package com.danieljgaull.texteditor.texteditor.util;

public class ObjectOrError<T> {

    private T object;
    private boolean isError;
    private String name;
    private String errorMessage;

    private ObjectOrError(T object, boolean isError, String name, String errorMessage) {
        this.object = object;
        this.isError = isError;
        this.name = name;
        this.errorMessage = errorMessage;
    }

    public static <T> ObjectOrError<T> object(T object, String name) {
        return new ObjectOrError<>(object, false, name, null);
    }
    public static <T> ObjectOrError<T> error(String name, String errorMessage) {
        return new ObjectOrError<T>(null, true, name, errorMessage);
    }

    public T getObject() {
        return object;
    }

    public boolean isError() {
        return isError;
    }

    public String getName() {
        return name;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
