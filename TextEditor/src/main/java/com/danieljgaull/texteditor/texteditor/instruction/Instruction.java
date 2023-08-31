package com.danieljgaull.texteditor.texteditor.instruction;

import java.util.List;

public class Instruction {

    private InstructionTypes type;
    private List<String> args;

    public Instruction(InstructionTypes type, List<String> args) {
        this.type = type;
        this.args = args;
    }

    public InstructionTypes getType() {
        return type;
    }
    public List<String> getArgs() {
        return args;
    }
}
