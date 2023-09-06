package com.danieljgaull.texteditor.texteditor.util;

public class StringUtils {

    // Force a static class
    private StringUtils() {}

    public static int countChar(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

}
