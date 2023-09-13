package com.danieljgaull.texteditor.texteditor.plugin;

import com.danieljgaull.texteditor.texteditor.macro.Macro;
import com.danieljgaull.texteditor.texteditor.modes.Mode;

import java.util.List;

public class Plugin {

    private String name;
    private List<Macro> macros;
    private List<Mode> modes;

    public Plugin(String name, List<Macro> macros, List<Mode> modes) {
        this.name = name;
        this.macros = macros;
        this.modes = modes;
    }

    public String getName() {
        return name;
    }

    public List<Macro> getMacros() {
        return macros;
    }

    public List<Mode> getModes() {
        return modes;
    }
}
