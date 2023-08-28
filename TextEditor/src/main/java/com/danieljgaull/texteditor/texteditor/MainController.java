package com.danieljgaull.texteditor.texteditor;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class MainController {
    @FXML
    private TextArea mainField;
    @FXML
    private ProgressBar progressBar;

    public void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            doOpenFile(file);
        }
    }

    private void doOpenFile(File file) {
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
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
            try {
                mainField.setText(loadFileTask.get());
            } catch (InterruptedException | ExecutionException e) {
                // TODO... status text
            }
        });
        loadFileTask.setOnFailed(workerStateEvent -> {
            // TODO... status text
        });
        progressBar.progressProperty().bind(loadFileTask.progressProperty());
        loadFileTask.run();
    }
}