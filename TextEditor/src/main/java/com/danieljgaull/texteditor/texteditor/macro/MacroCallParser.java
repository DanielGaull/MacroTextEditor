package com.danieljgaull.texteditor.texteditor.macro;

import com.danieljgaull.texteditor.texteditor.data.DataValue;

import java.util.ArrayList;
import java.util.List;

public class MacroCallParser {

    private List<Macro> macros;

    private static final char ARG_WRAPPER_START = '(';
    private static final char ARG_WRAPPER_END = ')';
    private static final char ARG_SEPARATOR = ',';

    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";
    private static final String STRING_WRAPPER = "\"";

    public MacroCallParser(List<Macro> macros) {
        this.macros = macros;
    }

    public MacroCall parse(String input) {
        String name;
        String[] args;
        // Need to get the name, and get the arguments
        int firstParenIndex = input.indexOf(ARG_WRAPPER_START);
        int lastParenIndex = input.lastIndexOf(ARG_WRAPPER_END);
        // If no parens provided, then we have an empty arg list
        if (firstParenIndex < 0 || lastParenIndex < 0) {
            name = input;
            args = new String[0];
        } else {
            name = input.substring(0, firstParenIndex);
            String argString = input.substring(firstParenIndex + 1, lastParenIndex);
            if (argString.length() > 0) {
                args = argString.split(Character.toString(ARG_SEPARATOR));
            } else {
                args = new String[0];
            }
        }

        // Find the macro with this name
        Macro calledMacro = null;
        for (Macro m : macros) {
            if (m.getName().equals(name)) {
                calledMacro = m;
                break;
            }
        }
        if (calledMacro == null) {
            // TODO: Throw macro not found error
            return null;
        }

        // Now need to parse the args
        List<DataValue> argValues = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            argValues.add(parseArg(args[i]));
        }

        // If # or type of args doesn't match macro definition, throw exception
        if (calledMacro.getParameters().size() != argValues.size()) {
            // TODO: exception
            return null;
        }
        for (int i = 0; i < calledMacro.getParameters().size(); i++) {
            MacroParameter param = calledMacro.getParameters().get(i);
            DataValue arg = argValues.get(i);
            if (param.type != arg.getType()) {
                // TODO: exception
                return null;
            }
        }

        return new MacroCall(calledMacro, argValues);
    }

    private DataValue parseArg(String arg) {
        if (arg.equals(TRUE_STRING)) {
            return DataValue.bool(true);
        } else if (arg.equals(FALSE_STRING)) {
            return DataValue.bool(false);
        } else if (arg.startsWith(STRING_WRAPPER)) {
            return DataValue.string(arg.substring(0, arg.length() - 1));
        } else {
            // Must be a number
            // TODO: Catch arg exception and throw a custom exception
            return DataValue.number(Double.parseDouble(arg));
        }
    }

}
