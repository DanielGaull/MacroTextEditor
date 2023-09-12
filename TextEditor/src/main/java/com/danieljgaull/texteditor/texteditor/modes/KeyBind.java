package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.instruction.Instruction;

import java.util.List;

public class KeyBind {

    private List<KeyBindCodes> keys;
    private List<Instruction> instructions;

    public KeyBind(List<KeyBindCodes> keys, List<Instruction> instructions) {
        this.keys = keys;
        this.instructions = instructions;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<KeyBindCodes> getKeys() {
        return keys;
    }
}
