package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.handlers.Action;
import com.danieljgaull.texteditor.texteditor.handlers.FileContentsLoadedHandler;
import com.danieljgaull.texteditor.texteditor.handlers.StatusMessageHandler;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class TextEditorController {

    private StatusMessageHandler statusMessageHandler;

    private File currentLoadedFile;

    public TextEditorController(StatusMessageHandler statusMessageHandler) {
        this.statusMessageHandler = statusMessageHandler;
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
//                progressBar.setVisible(true);
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
            //progressBar.setVisible(false);
            onSuccess.call();
            try {
                handler.handle(loadFileTask.get());
                statusMessageHandler.handle("Opened " + file.getName());
                currentLoadedFile = file;
            } catch (InterruptedException | ExecutionException e) {
                statusMessageHandler.handle("Error opening file");
            }
        });
        loadFileTask.setOnFailed(workerStateEvent -> {
            //progressBar.setVisible(false);
            onFail.call();
            statusMessageHandler.handle("Error opening file");
        });
        for (DoubleProperty prop : bindProperties) {
            prop.bind(loadFileTask.progressProperty());
        }
//        progressBar.progressProperty().bind(loadFileTask.progressProperty());
        loadFileTask.run();
    }

}
