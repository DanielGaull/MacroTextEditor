package com.danieljgaull.texteditor.texteditor.evaluation;

import com.danieljgaull.texteditor.texteditor.expressions.Ast;
import com.danieljgaull.texteditor.texteditor.expressions.BinaryOperators;
import com.danieljgaull.texteditor.texteditor.expressions.TernaryOperators;
import com.danieljgaull.texteditor.texteditor.expressions.UnaryOperators;
import com.danieljgaull.texteditor.texteditor.util.Truple;
import com.danieljgaull.texteditor.texteditor.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {

    private final Pattern NUMBER_REGEX = Pattern.compile("^[+-]?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))$");
    // This is the raw regex: ^"((?:[^"]|\\")*)"$
    // The backslashes make it hard to see what's going on
    // Makes a group, capturing any non-quote character OR a \"
    private final Pattern STRING_REGEX = Pattern.compile("^\"((?:[^\"]|\\\\\")*)\"$");

    private final String TRUE_VALUE = "true";
    private final String FALSE_VALUE = "false";
    private final Pattern BOOLEAN_REGEX = Pattern.compile("^(" + TRUE_VALUE + "|" + FALSE_VALUE + ")$");

    private final String TOKEN_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";
    private final Pattern TOKEN_REGEX = Pattern.compile("^(" + TOKEN_PATTERN + ")$");

    private final Pattern FUNCTION_CALL_REGEX = Pattern.compile("^(" + TOKEN_PATTERN + ")\\((.*)\\)$");
    private final char FUNCTION_ARG_SPLITTER = ',';

    private final List<Tuple<String, BinaryOperators>> binaryOperators = List.of(
            new Tuple<>("+", BinaryOperators.Addition),
            new Tuple<>("-", BinaryOperators.Subtraction),
            new Tuple<>("*", BinaryOperators.Multiplication),
            new Tuple<>("/", BinaryOperators.Division),
            new Tuple<>("&&", BinaryOperators.LogicalAnd),
            new Tuple<>("||", BinaryOperators.LogicalOr),
            new Tuple<>("==", BinaryOperators.IsEqual),
            new Tuple<>("!=", BinaryOperators.IsNotEqual),
            new Tuple<>(">", BinaryOperators.IsGreaterThan),
            new Tuple<>("<", BinaryOperators.IsLessThan),
            new Tuple<>(">=", BinaryOperators.IsGreaterThanOrEqual),
            new Tuple<>("<=", BinaryOperators.IsLessThanOrEqual)
    );
    private final List<Tuple<String, UnaryOperators>> unaryOperators = List.of(
            new Tuple<>("!", UnaryOperators.LogicalNegation),
            new Tuple<>("-", UnaryOperators.NumericNegation)
    );
    private final List<Truple<String, String, TernaryOperators>> ternaryOperators = List.of(
            new Truple<>("?", ":", TernaryOperators.Conditional)
    );

    public Ast parse(String input) {
        Matcher numberMatcher = NUMBER_REGEX.matcher(input);
        if (numberMatcher.find()) {
            String numStr = numberMatcher.group();
            double num = Double.parseDouble(numStr);
            return Ast.number(num);
        }

        Matcher stringMatcher = STRING_REGEX.matcher(input);
        if (stringMatcher.find()) {
            String value = stringMatcher.group();
            return Ast.string(value);
        }

        Matcher boolMatcher = BOOLEAN_REGEX.matcher(input);
        if (boolMatcher.find()) {
            String value = boolMatcher.group();
            if (value.equals(TRUE_VALUE)) {
                return Ast.bool(true);
            }
            else if (value.equals(FALSE_VALUE)) {
                return Ast.bool(false);
            }
            // We should never get here; parsing error
            return null;
        }

        Matcher tokenMatcher = TOKEN_REGEX.matcher(input);
        if (tokenMatcher.find()) {
            String value = tokenMatcher.group();
            return Ast.variable(value);
        }

        Matcher funcMatcher = FUNCTION_CALL_REGEX.matcher(input);
        if (funcMatcher.find()) {
            String funcName = funcMatcher.group(1);
            List<String> args = split(funcMatcher.group(2), FUNCTION_ARG_SPLITTER);
            List<Ast> argAsts = new ArrayList<>();
            for (String arg : args) {
                argAsts.add(parse(arg.trim()));
            }
            return Ast.functionCall(funcName, argAsts);
        }

        return null;
    }

    private List<String> split(String source, char delimiter) {
        int parenLevel = 0;
        int bracketLevel = 0;
        int braceLevel = 0;
        boolean inString = false;
        List<String> results = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (inString) {
                if (c == '"' && source.charAt(i-1) != '\\') {
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

}
