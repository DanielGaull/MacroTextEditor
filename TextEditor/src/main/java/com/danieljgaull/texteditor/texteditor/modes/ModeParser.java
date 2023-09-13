package com.danieljgaull.texteditor.texteditor.modes;

import com.danieljgaull.texteditor.texteditor.data.DataTypes;
import com.danieljgaull.texteditor.texteditor.data.DataValue;
import com.danieljgaull.texteditor.texteditor.data.Variable;
import com.danieljgaull.texteditor.texteditor.data.VariableData;
import com.danieljgaull.texteditor.texteditor.expressions.Ast;
import com.danieljgaull.texteditor.texteditor.expressions.ExpressionEvaluator;
import com.danieljgaull.texteditor.texteditor.expressions.ExpressionParser;
import com.danieljgaull.texteditor.texteditor.instruction.Instruction;
import com.danieljgaull.texteditor.texteditor.instruction.InstructionParser;
import com.danieljgaull.texteditor.texteditor.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ModeParser {

    private ExpressionParser expressionParser;
    private ExpressionEvaluator expressionEvaluator;
    private InstructionParser instructionParser;

    public ModeParser() {
        expressionParser = new ExpressionParser();
        expressionEvaluator = new ExpressionEvaluator();
        instructionParser = new InstructionParser();
    }

    // Should include all the way from the "mode" statement to the "endmode" statement
    public Mode parse(List<String> lines) {
        String header = lines.get(0);
        String name = header.substring(header.indexOf(' ') + 1).trim();

        // Now need to go through and parse everything
        List<ModeVariable> variables = new ArrayList<>();
        List<KeyBind> keyBinds = new ArrayList<>();
        Ast prefix = Ast.string("");
        Ast suffix = Ast.string("");
        // Set to 1 to skip header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.length() > 0) {
                // If the line starts with 'var', it's a variable
                // 'bind' -> Key bind
                // 'prefix'/'suffix' sets those expressions
                if (line.startsWith("bind")) {
                    // Need to find the "endbind" statement and get all those lines to send to parseKeyBind
                    List<String> bindLines = new ArrayList<>();
                    while (!lines.get(i).trim().equals("endbind")) {
                        bindLines.add(lines.get(i));
                        i++;
                        if (i >= lines.size()) {
                            throw new IllegalArgumentException("'bind' statement is missing corresponding 'endbind'");
                        }
                    }
                    keyBinds.add(parseKeyBind(bindLines));
                } else if (line.startsWith("var")) {
                    variables.add(parseVar(line));
                } else if (line.startsWith("prefix")) {
                    // Everything after the space is the prefix expression
                    prefix = expressionParser.parse(line.substring(line.indexOf(' ') + 1).trim());
                } else if (line.startsWith("suffix")) {
                    suffix = expressionParser.parse(line.substring(line.indexOf(' ') + 1).trim());
                } else if (!line.equals("endmode")) {
                    // Illegal line
                    throw new IllegalArgumentException("The line \"" + line + "\" is invalid in mode definitions");
                }
            }
        }

        return new Mode(name, variables, keyBinds, prefix, suffix);
    }

    private ModeVariable parseVar(String line) {
        List<String> tokens = StringUtils.split(line, ' ');
        if (tokens.size() != 4) {
            throw new IllegalArgumentException("Proper usage is: var [name] [type] [default value]");
        }
        String varName = tokens.get(1);
        String typeStr = tokens.get(2);
        String defStr = tokens.get(3);
        DataTypes type = DataTypes.parse(typeStr);

        // Need to parse and evaluate the default value
        Ast defaultValueExpression = expressionParser.parse(defStr);
        DataValue defaultValue = expressionEvaluator.evaluate(defaultValueExpression, new VariableData());

        return new ModeVariable(new Variable(varName, type), defaultValue);
    }
    private KeyBind parseKeyBind(List<String> lines) {
        String header = lines.get(0);
        List<KeyBindCodes> keyCode = new ArrayList<>();
        String keyCodeString = header.substring(header.indexOf(' ') + 1).trim();
        // Split on pluses, trim each piece, and match to the keycode array
        String[] rawKeyCodes = keyCodeString.split("\\+");
        for (int i = 0; i < rawKeyCodes.length; i++) {
            String rawCode = rawKeyCodes[i].trim();
            // Try matching it to an actual key code
            boolean foundCode = false;
            for (KeyBindCodes code : KeyBindCodes.values()) {
                String name = code.name();
                if (rawCode.equalsIgnoreCase(name) || ("_" + rawCode).equalsIgnoreCase(name)) {
                    keyCode.add(code);
                    foundCode = true;
                    break;
                }
            }
            if (!foundCode) {
                throw new IllegalArgumentException("The string '" + rawCode + "' does not correspond to any valid key code.");
            }
        }

        List<Instruction> instructions = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.length() > 0 && !line.equals("endbind")) {
                instructions.add(instructionParser.parse(line));
            }
        }

        return new KeyBind(keyCode, instructions);
    }

}
