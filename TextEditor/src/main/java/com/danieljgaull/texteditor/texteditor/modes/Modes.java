package com.danieljgaull.texteditor.texteditor.modes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Modes {

    private List<Mode> modes;

    public Modes() {
        modes = new ArrayList<>();
        // Add the default normal mode
        modes.add(new Mode("Default"));
    }

    public Mode getMode(String name) {
        for (Mode mode : modes) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

}
