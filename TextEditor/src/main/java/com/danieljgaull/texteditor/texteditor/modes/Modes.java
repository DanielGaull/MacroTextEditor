package com.danieljgaull.texteditor.texteditor.modes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Modes {

    private List<Mode> modes;
    public static final String DEFAULT_MODE_NAME = "Default";

    public Modes(List<Mode> modes) {
        modes = new ArrayList<>(modes);
        // Add the default normal mode
        modes.add(new Mode(DEFAULT_MODE_NAME));
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
