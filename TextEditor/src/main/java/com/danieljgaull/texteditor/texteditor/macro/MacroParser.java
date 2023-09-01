package com.danieljgaull.texteditor.texteditor.macro;

import com.danieljgaull.texteditor.texteditor.expressions.DataTypes;
import com.danieljgaull.texteditor.texteditor.instruction.Instruction;
import com.danieljgaull.texteditor.texteditor.instruction.InstructionParser;

import java.util.ArrayList;
import java.util.List;

public class MacroParser {

    private static final char OPEN_PAREN = '(';
    private static final char CLOSE_PAREN = ')';

    private static final char PARAM_NAME_TYPE_SEPARATOR = ':';
    private static final char PARAMS_DELIMITER = ',';

    // Should include all the way from the "macro" statement to the "endmacro" statement
    public Macro parse(String input) {
        String[] lines = input.split("\n");
        String header = lines[0];
        int headerOpenParenPos = header.indexOf(OPEN_PAREN);
        int headerCloseParenPos = header.indexOf(CLOSE_PAREN);
        String name = header.substring(header.indexOf(' ') + 1, headerOpenParenPos).trim();
        String paramString = header.substring(headerOpenParenPos + 1, headerCloseParenPos - 1).trim();
        List<MacroParameter> parameters = parseParameters(paramString);

        List<Instruction> instructions = new ArrayList<>();
        InstructionParser instParser = new InstructionParser();
        // Ignore first and last lines for instructions (includes endmacro line)
        for (int i = 1; i < lines.length - 1; i++) {
            instructions.add(instParser.parse(lines[i]));
        }

        return new Macro(name, parameters, instructions);
    }

    private List<MacroParameter> parseParameters(String paramString) {
        String[] paramEntries = paramString.split("" + PARAMS_DELIMITER);
        List<MacroParameter> params = new ArrayList<>();
        for (int i = 0; i < paramEntries.length; i++) {
            params.add(parseParameter(paramEntries[i].trim()));
        }
        return params;
    }
    private MacroParameter parseParameter(String paramString) {
        int midIndex = paramString.indexOf(PARAM_NAME_TYPE_SEPARATOR);
        String name = paramString.substring(0, midIndex).trim();
        String type = paramString.substring(midIndex + 1).trim();
        return new MacroParameter(name, DataTypes.parse(type));
    }

}
