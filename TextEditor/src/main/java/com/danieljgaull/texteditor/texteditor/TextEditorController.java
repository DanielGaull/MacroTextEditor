package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.expressions.Ast;
import com.danieljgaull.texteditor.texteditor.expressions.ExpressionEvaluator;
import com.danieljgaull.texteditor.texteditor.expressions.ExpressionParser;
import com.danieljgaull.texteditor.texteditor.handlers.Action;
import com.danieljgaull.texteditor.texteditor.handlers.FileContentsLoadedHandler;
import com.danieljgaull.texteditor.texteditor.handlers.MessageHandler;
import com.danieljgaull.texteditor.texteditor.instruction.Instruction;
import com.danieljgaull.texteditor.texteditor.instruction.InstructionTypes;
import com.danieljgaull.texteditor.texteditor.macro.Macro;
import com.danieljgaull.texteditor.texteditor.macro.MacroCall;
import com.danieljgaull.texteditor.texteditor.macro.MacroCallParser;
import com.danieljgaull.texteditor.texteditor.modes.Modes;
import com.danieljgaull.texteditor.texteditor.data.DataValue;
import com.danieljgaull.texteditor.texteditor.data.VariableData;
import com.danieljgaull.texteditor.texteditor.plugin.Plugin;
import com.danieljgaull.texteditor.texteditor.text.TextChange;
import com.danieljgaull.texteditor.texteditor.text.TextLine;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class TextEditorController {

    private MessageHandler statusMessageHandler;
    private MessageHandler titleChangeHandler;
    private MessageHandler modeChangeHandler;

    private File currentLoadedFile;
    private boolean isDirty;
    private Modes modes;

    private List<TextLine> lines;
    private int caret;
    private int anchor;

    private List<Macro> macros;
    private MacroCallParser macroCallParser;

    private ExpressionParser exprParser;
    private ExpressionEvaluator exprEvaluator;

    public TextEditorController(MessageHandler statusMessageHandler, MessageHandler titleChangeHandler,
                                MessageHandler modeChangeHandler, List<Plugin> plugins) {
        this.statusMessageHandler = statusMessageHandler;
        this.titleChangeHandler = titleChangeHandler;
        this.modeChangeHandler = modeChangeHandler;

        currentLoadedFile = null;
        clearDirty();
        modes = new Modes();

        //modeChangeHandler.handle(modes.getMode().getName());

        lines = new ArrayList<>();
        // Add our first, empty line
        lines.add(new TextLine(modes.getMode("Default"), "", new VariableData()));

        macros = new ArrayList<>();
        macroCallParser = new MacroCallParser(macros);

        exprParser = new ExpressionParser();
        exprEvaluator = new ExpressionEvaluator();
    }

    public void makeDirty() {
        isDirty = true;
        changeTitle("*");
    }
    public void clearDirty() {
        isDirty = false;
        changeTitle("");
    }
    private void changeTitle(String postfix) {
        if (currentLoadedFile != null) {
            titleChangeHandler.handle(currentLoadedFile.getName() + postfix + " - Text Editor");
        } else {
            titleChangeHandler.handle("New File" + postfix + " - Text Editor");
        }
    }

    public void handleTextChange(TextChange change, int lineCaretPos, int linePos) {
        if (!change.isAnythingChanged()) {
            // Nothing was changed, so we should just return immediately
            return;
        }
        makeDirty();
        // TODO: make sure we take off the prefix/suffix text in calculating lineCaretPos

        if (change.isNewLine()) {
            // Just need to add a new line
            TextLine line = lines.get(linePos);
            String currentLineText = line.getRawText().substring(0, lineCaretPos);
            String newLineText = line.getRawText().substring(lineCaretPos);
            lines.add(linePos + 1, new TextLine(line.getLineMode(), newLineText, line.copyLineData()));
            line.setRawText(currentLineText);
        } else {
            // Insert the newly-typed text at this location
            TextLine line = lines.get(linePos);
            String lineText = line.getRawText();
            if (change.isDelete()) {
                // First, remove the deleted range. Then we can add the new text
                lineText = lineText.substring(0, change.getRangeStart()) + lineText.substring(change.getRangeEnd());
            }
            if (change.isLineDelete()) {
                TextLine startLine = lines.get(change.getLineStart());
                TextLine endLine = lines.get(change.getLineEnd());
                String newStartLineText = startLine.getRawText().substring(0, change.getRangeStart()) +
                        endLine.getRawText().substring(change.getRangeEnd());
                startLine.setRawText(newStartLineText);
                lineText = newStartLineText;
                // Need to delete the specified lines
                for (int i = change.getLineEnd(); i > change.getLineStart(); i--) {
                    lines.remove(i);
                }
            }
            String textBefore = lineText.substring(0, lineCaretPos);
            String textAfter = lineText.substring(lineCaretPos);
            if (change.getTextLines().isEmpty()) {
                String newLineText = textBefore + change.getText() + textAfter;
                line.setRawText(newLineText);
            } else {
                // Add the first bit of text to our existing line
                // Then, create lines for each of the other lines
                // The last line will get the textAfter appended to it
                line.setRawText(textBefore + change.getTextLines().get(0));
                for (int i = 1; i < change.getTextLines().size(); i++) {
                    String newLine = change.getTextLines().get(i);
                    lines.add(linePos + 1, new TextLine(line.getLineMode(), newLine, line.copyLineData()));
                    linePos++;
                }
                // Add textAfter to the last line
                lines.get(linePos).setRawText(lines.get(linePos).getRawText() + textAfter);
            }
        }
    }

    public String buildText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            builder.append(lines.get(i).getRawText());
            if (i + 1 < lines.size()) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    public void setCaretAndAnchor(int caret, int anchor) {
        this.caret = caret;
        this.anchor = anchor;
    }

    public int getCaret() {
        return caret;
    }
    public int getAnchor() {
        return anchor;
    }

    public void runMacro(String text, int line, int lineCaret) {
        MacroCall call = macroCallParser.parse(text);
        VariableData args = new VariableData();
        for (int i = 0; i < call.args().size(); i++) {
            args.setValue(call.macro().getParameters().get(i).name, call.args().get(i));
        }
        for (int i = 0; i < call.macro().getInstructions().size(); i++) {
            Instruction inst = call.macro().getInstructions().get(i);
            runInstruction(inst, line, lineCaret, args);
        }
    }
    private void runInstruction(Instruction instruction, int line, int lineCaret, VariableData args) {
        // TODO: Add variable data (concat the macro args w/ line data
        VariableData vars = new VariableData();
        vars.concat(args);
        VariableData lineData = lines.get(line).getLineData();
        vars.concat(lineData);
        if (instruction.getType() == InstructionTypes.InsertText) {
            // Get the two args
            String preTextRaw = instruction.getArgs().get(0);
            Ast preTextAst = exprParser.parse(preTextRaw);
            DataValue preTextValue = exprEvaluator.evaluate(preTextAst, vars);
            String preText = preTextValue.toString();

            String postText = "";
            if (instruction.getArgs().size() > 1) {
                String postTextRaw = instruction.getArgs().get(1);
                Ast postTextAst = exprParser.parse(postTextRaw);
                DataValue postTextValue = exprEvaluator.evaluate(postTextAst, vars);
                postText = postTextValue.toString();
            }
            // Now add the text
            String raw = lines.get(line).getRawText();
            String textBefore = raw.substring(0, lineCaret);
            String textAfter = raw.substring(lineCaret);
            raw = textBefore + preText + postText + textAfter;
            lines.get(line).setRawText(raw);
            caret += preText.length();
            anchor += preText.length();
        }

    }

    public void save(String text) {
        try {
            if (currentLoadedFile == null) {
                doSaveAs(text);
                return;
            }
            doSaveFile(currentLoadedFile, text);
        } catch (IOException ex) {
            statusMessageHandler.handle("Error saving file");
        }
    }

    public void saveAs(String text) {
        try {
            doSaveAs(text);
        } catch (IOException ex) {
            statusMessageHandler.handle("Error saving file");
        }
    }

    public void open(FileContentsLoadedHandler handler,
                     Action onStart, Action onSuccess, Action onFail,
                     List<DoubleProperty> bindProperties) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            doOpenFile(file, handler, onStart, onSuccess, onFail, bindProperties);
        }
    }

    private void doSaveFile(File file, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(text);
        fileWriter.close();
        statusMessageHandler.handle("File saved to " + file.getName());
        clearDirty();
    }

    private void doSaveAs(String text) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            statusMessageHandler.handle("Invalid save file provided");
            return;
        }

        currentLoadedFile = file;
        doSaveFile(file, text);
    }

    private void doOpenFile(File file, FileContentsLoadedHandler handler,
                            Action onStart, Action onSuccess, Action onFail,
                            List<DoubleProperty> bindProperties) {
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
            onStart.call();
            statusMessageHandler.handle("Opening file...");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            long linesToRead;
            try (Stream<String> stream = Files.lines(file.toPath())) {
                linesToRead = stream.count();
            }
            StringBuilder fileText = new StringBuilder();
            long linesRead = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileText.append(line).append("\n");
                updateProgress(++linesRead, linesToRead);
            }
            return fileText.toString();
            }
        };
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            onSuccess.call();
            try {
                handler.handle(loadFileTask.get());
                statusMessageHandler.handle("Opened " + file.getName());
                currentLoadedFile = file;
                clearDirty();
            } catch (InterruptedException | ExecutionException e) {
                statusMessageHandler.handle("Error opening file");
            }
        });
        loadFileTask.setOnFailed(workerStateEvent -> {
            onFail.call();
            statusMessageHandler.handle("Error opening file");
        });
        for (DoubleProperty prop : bindProperties) {
            prop.bind(loadFileTask.progressProperty());
        }
        loadFileTask.run();
    }

}
