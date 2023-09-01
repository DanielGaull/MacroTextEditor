package com.danieljgaull.texteditor.texteditor.expressions;

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
import java.util.stream.Collectors;

public class ExpressionParser {

    private static final Pattern NUMBER_REGEX = Pattern.compile("^[+-]?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))$");
    // This is the raw regex: ^"((?:[^"]|\\")*)"$
    // The backslashes make it hard to see what's going on
    // Makes a group, capturing any non-quote character OR a \"
    private static final Pattern STRING_REGEX = Pattern.compile("^\"((?:[^\"]|\\\\\")*)\"$");

    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    private static final Pattern BOOLEAN_REGEX = Pattern.compile("^(" + TRUE_VALUE + "|" + FALSE_VALUE + ")$");

    private static final String TOKEN_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final Pattern TOKEN_REGEX = Pattern.compile("^(" + TOKEN_PATTERN + ")$");

    private static final Pattern FUNCTION_CALL_REGEX = Pattern.compile("^(" + TOKEN_PATTERN + ")\\((.*)\\)$");
    private static final char FUNCTION_ARG_SPLITTER = ',';

    private static final char OPEN_PAREN = '(';
    private static final char CLOSE_PAREN = ')';
    private static final char STRING_START = '"';
    private static final char STRING_END = '"';

    // The (.*\S+.*) is to match any string that contains non-whitespace, but will include the whitespace if found
    private static final Pattern TERNARY_CONDITIONAL_REGEX = Pattern.compile("^(\\S+.*)\\?(.*\\S+.*):(.*\\S+)$");

    private static final List<Tuple<String, BinaryOperators>> binaryOperators = List.of(
            new Tuple<>("*", BinaryOperators.Multiplication),
            new Tuple<>("/", BinaryOperators.Division),
            new Tuple<>("+", BinaryOperators.Addition),
            new Tuple<>("-", BinaryOperators.Subtraction),
            new Tuple<>("&&", BinaryOperators.LogicalAnd),
            new Tuple<>("||", BinaryOperators.LogicalOr),
            new Tuple<>("==", BinaryOperators.IsEqual),
            new Tuple<>("!=", BinaryOperators.IsNotEqual),
            new Tuple<>(">", BinaryOperators.IsGreaterThan),
            new Tuple<>("<", BinaryOperators.IsLessThan),
            new Tuple<>(">=", BinaryOperators.IsGreaterThanOrEqual),
            new Tuple<>("<=", BinaryOperators.IsLessThanOrEqual)
    );
    private static final List<Tuple<String, UnaryOperators>> unaryOperators = List.of(
            new Tuple<>("!", UnaryOperators.LogicalNegation),
            new Tuple<>("-", UnaryOperators.NumericNegation)
    );

    public Ast parse(String input) {
        if (input.length() <= 0) {
            return null;
        }
        // See if the entire expression is wrapped in parentheses
        if (input.charAt(0) == OPEN_PAREN) {
            int endPos = findClosingPosition(input, 0, OPEN_PAREN, CLOSE_PAREN);
            if (endPos == input.length() - 1) {
                // Extract the inner expression and parse that instead
                return parse(input.substring(1, input.length() - 1));
            }
        }

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

        Matcher ternaryMatcher = TERNARY_CONDITIONAL_REGEX.matcher(input);
        if (ternaryMatcher.find()) {
            String operand1 = ternaryMatcher.group(1);
            String operand2 = ternaryMatcher.group(2);
            String operand3 = ternaryMatcher.group(3);
            return Ast.ternary(parse(operand1.trim()), parse(operand2.trim()), parse(operand3.trim()),
                    TernaryOperators.Conditional);
        }

        // At this point we know we have some sort of operator expression. Build the regexes
        // for each and determine what we have
        String unaryOpPattern = "^(" +
                unaryOperators.stream()
                        .map(t -> escapeAll(t.first())) // Escape each character
                        .collect(Collectors.joining("|")) +
                ")(.*\\S+)$";
        Pattern unaryOpRegex = Pattern.compile(unaryOpPattern);
        Matcher unaryMatcher = unaryOpRegex.matcher(input);
        if (unaryMatcher.find()) {
            String operator = unaryMatcher.group(1);
            String operand = unaryMatcher.group(2);
            for (Tuple<String, UnaryOperators> tuple : unaryOperators) {
                if (tuple.first().equals(operator)) {
                    return Ast.unary(parse(operand.trim()), tuple.second());
                }
            }
            // Somehow failed even though operator should have been found??
            return null;
        }

        BinaryOperatorMatch binaryOperatorMatch = matchBinaryOperator(input);
        if (binaryOperatorMatch.matches) {
            String operand1 = binaryOperatorMatch.first;
            String operand2 = binaryOperatorMatch.second;
            return Ast.binary(parse(operand1.trim()), parse(operand2.trim()), binaryOperatorMatch.operator);
        }

        return null;
    }

    private static List<String> split(String source, char delimiter) {
        int parenLevel = 0;
        int bracketLevel = 0;
        int braceLevel = 0;
        boolean inString = false;
        List<String> results = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (inString) {
                if (c == STRING_END && source.charAt(i - 1) != '\\') {
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
                if (c == STRING_START) {
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

    private static String escapeAll(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result.append("\\").append(c);
        }
        return result.toString();
    }

    private static int findClosingPosition(String input, int startLoc, char start, char end) {
        int level = 0;
        boolean inString = false;
        for (int i = startLoc + 1; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inString) {
                if (c == STRING_END && input.charAt(i - 1) != '\\') {
                    inString = false;
                }
            } else {
                if (c == STRING_START) {
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

    private static BinaryOperatorMatch matchBinaryOperator(String input) {
        boolean inString = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inString) {
                if (c == STRING_END && input.charAt(i - 1) != '\\') {
                    inString = false;
                }
            } else {
                if (c == STRING_START) {
                    inString = true;
                    continue;
                }
                if (c == OPEN_PAREN) {
                    int endPoint = findClosingPosition(input, i, OPEN_PAREN, CLOSE_PAREN);
                    if (endPoint >= 0) {
                        i = endPoint;
                        continue;
                    } else {
                        // Return; we have unbalanced parentheses
                        break;
                    }
                }
                // See if there's an operator at this point. If so, make our groups
                for (Tuple<String, BinaryOperators> tuple : binaryOperators) {
                    if (input.substring(i).startsWith(tuple.first())) {
                        // We've got a match
                        // The first part is from the beginning of the string to this location
                        String first = input.substring(0, i);
                        String second = input.substring(i + tuple.first().length());
                        return new BinaryOperatorMatch(tuple.second(), first, second);
                    }
                }
            }
        }
        return new BinaryOperatorMatch();
    }

    private static class BinaryOperatorMatch {
        boolean matches;
        BinaryOperators operator;
        String first;
        String second;

        private BinaryOperatorMatch() {
            matches = false;
        }
        private BinaryOperatorMatch(BinaryOperators op, String f, String s) {
            operator = op;
            first = f;
            second = s;
            matches = true;
        }
    }
}
