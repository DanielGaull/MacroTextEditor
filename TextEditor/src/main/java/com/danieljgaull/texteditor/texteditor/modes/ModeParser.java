package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.data.Variable;

import java.util.ArrayList;
import java.util.List;

public class ModeParser {

    // Should include all the way from the "mode" statement to the "endmode" statement
    public Mode parse(String input) {
        String[] lines = input.split("\n");

        String header = lines[0];
        String name = header.substring(header.indexOf(' ') + 1).trim();

        // Now need to go through and parse everything
        List<Variable> variables = new ArrayList<>();
        List<KeyBind> keyBinds = new ArrayList<>();
        String prefix = "";
        String suffix = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().length() > 0) {
                
            }
        }

        return null;
    }

}
