package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.macro.Macro;

public class ModeParser {

    // Should include all the way from the "mode" statement to the "endmode" statement
    public Mode parse(String input) {
        String[] lines = input.split("\n");

        String header = lines[0];
        String name = header.substring(header.indexOf(' ') + 1).trim();

        // Now need to go through and parse everything
        

        return null;
    }

}
