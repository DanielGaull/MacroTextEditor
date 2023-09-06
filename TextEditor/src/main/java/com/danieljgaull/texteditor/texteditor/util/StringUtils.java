package com.danieljgaull.texteditor.texteditor.util;

public class StringUtils {

    // Force a static class
    private StringUtils() {}

    public static int countChar(String str, char c) {
        return countChar(str, str.length(), c);
    }
    public static int countChar(String str, int endIndex, char c) {
        int count = 0;
        for (int i = 0; i < endIndex; i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static int lastIndexOfChar(String str, int endIndex, char c) {
        int lastIndex = -1;
        for (int i = 0; i < endIndex; i++) {
            if (str.charAt(i) == c) {
                lastIndex = i;
            }
        }
        return lastIndex;
    }

}
