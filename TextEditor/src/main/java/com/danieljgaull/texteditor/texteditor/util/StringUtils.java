package com.danieljgaull.texteditor.texteditor.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    // Force a static class
    private StringUtils() {}

    public static int countChar(String str, char c) {
        return countChar(str, str.length(), c);
    }
    public static int countChar(String str, int endIndex, char c) {
        int count = 0;
        for (int i = 0; i < endIndex && i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static int lastIndexOfChar(String str, int endIndex, char c) {
        int lastIndex = -1;
        for (int i = 0; i < endIndex && i < str.length(); i++) {
            if (str.charAt(i) == c) {
                lastIndex = i;
            }
        }
        return lastIndex;
    }

    public static List<String> split(String source, char delimiter) {
        int parenLevel = 0;
        int bracketLevel = 0;
        int braceLevel = 0;
        boolean inString = false;
        List<String> results = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (inString) {
                if (c == '"' && source.charAt(i - 1) != '\\' && source.charAt(i - 2) != '\\') {
                    // We've found our closing quote, and it has not been escaped
                    inString = false;
                }
            } else {
                if (c == '(') {
                    parenLevel++;
                }
                if (c == '[') {
                    bracketLevel++;
                }
                if (c == '{') {
                    braceLevel++;
                }
                if (c == ')') {
                    parenLevel--;
                }
                if (c == ']') {
                    bracketLevel--;
                }
                if (c == '}') {
                    braceLevel--;
                }
                if (c == '"') {
                    inString = true;
                }
            }
            // If we find the delimiter, then complete our current string and start a new one
            if (c == delimiter && parenLevel == 0 && braceLevel == 0 && bracketLevel == 0 && !inString) {
                results.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        // Make sure to add the last thing! i.e. the value we have in current right now
        results.add(current.toString());
        return results;
    }

    public static String escapeAll(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result.append("\\").append(c);
        }
        return result.toString();
    }

    public static int findClosingPosition(String input, int startLoc, char start, char end) {
        int level = 0;
        boolean inString = false;
        for (int i = startLoc + 1; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inString) {
                if (c == '"' && input.charAt(i - 1) != '\\' && input.charAt(i - 2) != '\\') {
                    inString = false;
                }
            } else {
                if (c == '"') {
                    inString = true;
                }
                if (c == start) {
                    level++;
                }
                if (c == end) {
                    if (level == 0) {
                        return i;
                    }
                    level--;
                }
            }
        }
        return -1;
    }

}
