package com.danieljgaull.texteditor.texteditor.instruction;

import com.danieljgaull.texteditor.texteditor.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class InstructionParser {

    private static final char OPEN_PAREN = '(';
    private static final char CLOSE_PAREN = ')';
    private static final char INSTRUCTION_ARG_SEPARATOR = ' ';
    private static final char STRING_START = '"';
    private static final char STRING_END = '"';

    private final List<Tuple<String, InstructionTypes>> instructionNames = List.of(
            new Tuple<>("mode", InstructionTypes.ChangeMode),
            new Tuple<>("insert", InstructionTypes.InsertText),
            new Tuple<>("set", InstructionTypes.SetVariable)
    );

    public Instruction parse(String input) {
        String instructionName = input.substring(0, input.indexOf(' '));
        InstructionTypes type = null;
        for (Tuple<String, InstructionTypes> tuple : instructionNames) {
            if (tuple.first().equals(instructionName)) {
                type = tuple.second();
                break;
            }
        }
        if (type == null) {
            // Error; invalid instruction name
            return null;
        }

        // Now just get the rest of the string and split the args
        String argString = input.substring(input.indexOf(' ') + 1);
        List<Integer> argSplitPositions = identifySplitPositions(argString, INSTRUCTION_ARG_SEPARATOR);
        List<String> argsList = splitOnIndexes(argString, argSplitPositions);
        return new Instruction(type, argsList);
    }

    private static List<Integer> identifySplitPositions(String input, char splitter) {
        List<Integer> result = new ArrayList<>();
        int parenDepth = 0;
        boolean inString = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inString) {
                if (c == STRING_END && input.charAt(i - 1) != '\\' && input.charAt(i - 2) != '\\') {
                    inString = false;
                }
            } else {
                if (c == OPEN_PAREN) {
                    parenDepth++;
                }
                if (c == CLOSE_PAREN) {
                    parenDepth--;
                }
                if (c == STRING_START) {
                    inString = true;
                    continue;
                }
                if (parenDepth == 0 && c == splitter) {
                    result.add(i);
                }
            }
        }
        return result;
    }
    private static List<String> splitOnIndexes(String input, List<Integer> indexes) {
        List<String> results = new ArrayList<>();
        if (indexes.size() <= 0) {
            results.add(input);
            return results;
        }
        // Add the first one; from the start to the first split position
        results.add(input.substring(0, indexes.get(0)));
        // Add the rest of them
        for (int i = 0; i < indexes.size(); i++) {
            int index = indexes.get(i);
            if (i + 1 < indexes.size()) {
                results.add(input.substring(index + 1, indexes.get(i + 1)));
            } else {
                results.add(input.substring(index + 1));
            }
        }
        return results;
    }
}
