package com.danieljgaull.texteditor.texteditor.evaluation;

import com.danieljgaull.texteditor.texteditor.expressions.Ast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {

    private final Pattern NUMBER_REGEX = Pattern.compile("^[+-]?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))$");

    public Ast parse(String input) {
        // Check if a literal number
        Matcher numberMatcher = NUMBER_REGEX.matcher(input);
        if (numberMatcher.find()) {
            String numStr = numberMatcher.group();
            double num = Double.parseDouble(numStr);
            return Ast.number(num);
        }
        return null;
    }

}
