package com.danieljgaull.texteditor.texteditor.instruction;

import com.danieljgaull.texteditor.texteditor.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class InstructionParser {

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
        String[] args = argString.split(" ");
        List<String> argsList = List.of(args);
        return new Instruction(type, argsList);
    }

}
