package com.danieljgaull.texteditor.texteditor.modes;

import java.util.ArrayList;
import java.util.List;

public class Modes {

    private List<Mode> modes;
    private Mode currentMode;

    public Modes() {
        modes = new ArrayList<>();
        // Add the default normal mode
        modes.add(new Mode("Default"));
        currentMode = modes.get(0);
    }

    public Mode getMode() {
        return currentMode;
    }

}
