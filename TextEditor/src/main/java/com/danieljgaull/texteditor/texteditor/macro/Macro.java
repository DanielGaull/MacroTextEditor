package com.danieljgaull.texteditor.texteditor.macro;

import com.danieljgaull.texteditor.texteditor.instruction.Instruction;

import java.util.List;

public class Macro {

    private String name;
    private List<MacroParameter> parameters;
    private List<Instruction> instructions;

    public Macro(String name, List<MacroParameter> parameters, List<Instruction> instructions) {
        this.name = name;
        this.parameters = parameters;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public List<MacroParameter> getParameters() {
        return parameters;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }
}
