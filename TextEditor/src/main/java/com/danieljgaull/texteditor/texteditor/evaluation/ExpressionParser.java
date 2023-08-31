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
import java.util.stream.Collectors;

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

    // The (\s*\S+\s*) is to match any string that contains non-whitespace, but will include the whitespace if found
    private final Pattern TERNARY_CONDITIONAL_REGEX = Pattern.compile("^(\\S+.*)\\?(.*\\S+.*):(.*\\S+)$");

    private final List<Tuple<String, BinaryOperators>> binaryOperators = List.of(
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
    private final List<Tuple<String, UnaryOperators>> unaryOperators = List.of(
            new Tuple<>("!", UnaryOperators.LogicalNegation),
            new Tuple<>("-", UnaryOperators.NumericNegation)
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

        String binaryOpPattern = "^(\\S+.*)(" +
                binaryOperators.stream()
                        .map(t -> escapeAll(t.first())) // Escape each character
                        .collect(Collectors.joining("|")) +
                ")(.*\\S+)$";
        Pattern binaryOpRegex = Pattern.compile(binaryOpPattern);
        Matcher binaryMatcher = binaryOpRegex.matcher(input);
        if (binaryMatcher.find()) {
            String operand1 = binaryMatcher.group(1);
            String operator = binaryMatcher.group(2);
            String operand2 = binaryMatcher.group(3);
            for (Tuple<String, BinaryOperators> tuple : binaryOperators) {
                if (tuple.first().equals(operator)) {
                    return Ast.binary(parse(operand1.trim()), parse(operand2.trim()), tuple.second());
                }
            }
            // Somehow failed even though operator should have been found??
            return null;
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

    private String escapeAll(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result.append("\\").append(c);
        }
        return result.toString();
    }
}
