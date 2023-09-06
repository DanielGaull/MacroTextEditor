package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.handlers.Action;
import com.danieljgaull.texteditor.texteditor.handlers.FileContentsLoadedHandler;
import com.danieljgaull.texteditor.texteditor.handlers.MessageHandler;
import com.danieljgaull.texteditor.texteditor.modes.Mode;
import com.danieljgaull.texteditor.texteditor.modes.Modes;
import com.danieljgaull.texteditor.texteditor.text.LineData;
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

    public TextEditorController(MessageHandler statusMessageHandler, MessageHandler titleChangeHandler,
                                MessageHandler modeChangeHandler) {
        this.statusMessageHandler = statusMessageHandler;
        this.titleChangeHandler = titleChangeHandler;
        this.modeChangeHandler = modeChangeHandler;

        currentLoadedFile = null;
        clearDirty();
        modes = new Modes();

        //modeChangeHandler.handle(modes.getMode().getName());

        lines = new ArrayList<>();
        // Add our first, empty line
        lines.add(new TextLine(modes.getMode("Default"), "", new LineData()));
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
        makeDirty(); // TODO: Only make dirty if needed
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
            // TODO: this is where we'll detect macros (if the new text is only a backslash)
            TextLine line = lines.get(linePos);
            String lineText = line.getRawText();
            if (change.isDelete()) {
                // First, remove the deleted range. Then we can add the new text
                lineText = lineText.substring(0, change.getRangeStart()) + lineText.substring(change.getRangeEnd());
            }
            String textBefore = lineText.substring(0, lineCaretPos);
            String textAfter = lineText.substring(lineCaretPos);
            String newLineText = textBefore + change.getText() + textAfter;
            line.setRawText(newLineText);
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
