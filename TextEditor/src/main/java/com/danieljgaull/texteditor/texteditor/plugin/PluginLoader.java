package com.danieljgaull.texteditor.texteditor.plugin;

import com.danieljgaull.texteditor.texteditor.macro.Macro;
import com.danieljgaull.texteditor.texteditor.macro.MacroParser;
import com.danieljgaull.texteditor.texteditor.modes.Mode;
import com.danieljgaull.texteditor.texteditor.modes.ModeParser;
import com.danieljgaull.texteditor.texteditor.util.FileUtils;
import com.danieljgaull.texteditor.texteditor.util.ObjectOrError;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PluginLoader {

    private final String loaderRoot;
    private final String pluginPath;
    private static final String PLUGIN_FILE_NAME = "plugins";

    private MacroParser macroParser;
    private ModeParser modeParser;

    public PluginLoader(String loaderRoot) {
        this.loaderRoot = loaderRoot;
        pluginPath = loaderRoot + File.separator + PLUGIN_FILE_NAME;

        macroParser = new MacroParser();
        modeParser = new ModeParser();
    }

    public List<ObjectOrError<Plugin>> loadPlugins() throws FileNotFoundException {
        List<ObjectOrError<Plugin>> result = new ArrayList<>();
        // Each line in the path points to a plugin file
        Scanner scanner = new Scanner(new File(pluginPath));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String name = "";
            int lastBackslash = line.lastIndexOf('\\');
            if (line.substring(lastBackslash + 1).contains(".")) {
                name = line.substring(lastBackslash + 1, line.lastIndexOf('.'));
            } else {
                name = line.substring(lastBackslash + 1);
            }
            try {
                Plugin p = loadPlugin(line, name);
                result.add(ObjectOrError.object(p, name));
            } catch (Exception ex) {
                // There was an error
                result.add(ObjectOrError.error(name, ex.getMessage()));
            }
        }
        return result;
    }

    private Plugin loadPlugin(String filePath, String name) throws FileNotFoundException {
        // Read the full text
        List<String> lines = FileUtils.readAllLines(filePath);
        // First pass; we're going to remove all comment lines and empty lines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().length() <= 0) {
                lines.remove(i);
                i--;
            }
            if (lines.get(i).startsWith("#")) {
                lines.remove(i);
                i--;
            }
        }
        List<Mode> modes = new ArrayList<>();
        List<Macro> macros = new ArrayList<>();
        // Now we want to begin properly loading in our stuff
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("macro")) {
                // Grab all the lines for this macro, so we can parse it
                List<String> macroLines = new ArrayList<>();
                while (!lines.get(i).trim().equals("endmacro")) {
                    if (i + 1 >= lines.size()) {
                        // We're missing the endmacro
                        throw new IllegalArgumentException("'macro' statement is missing corresponding 'endmacro'");
                    }
                    macroLines.add(lines.get(i));
                    i++;
                }
                Macro macro = macroParser.parse(macroLines);
                macros.add(macro);
            }
            if (lines.get(i).startsWith("mode")) {
                List<String> modeLines = new ArrayList<>();
                while (!lines.get(i).trim().equals("endmode")) {
                    if (i + 1 >= lines.size()) {
                        // We're missing the endmode
                        throw new IllegalArgumentException("'mode' statement is missing corresponding 'endmode'");
                    }
                    modeLines.add(lines.get(i));
                    i++;
                }
                Mode mode = modeParser.parse(modeLines);
                modes.add(mode);
            }
        }

        return new Plugin(name, macros, modes);
    }

}
