package com.danieljgaull.texteditor.texteditor;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class MainController {
    @FXML
    private TextArea mainField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusText;

    private File currentLoadedFile;

    public void initialize() {
        // Only set the progress bar visible when stuff is loading
        progressBar.setVisible(false);
    }

    /*
     * Bound methods
     */

    public void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            doOpenFile(file);
        }
    }

    public void saveFile(ActionEvent event) {
        try {
            if (currentLoadedFile == null) {
                doSaveAs();
                return;
            }
            doSaveFile(currentLoadedFile);
        } catch (IOException ex) {
            statusText.setText("Error saving file");
        }
    }

    public void saveAs(ActionEvent event) {
        try {
            doSaveAs();
        } catch (IOException ex) {
            statusText.setText("Error saving file");
        }
    }

    /*
     * Helper Methods
     */

    private void doOpenFile(File file) {
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                progressBar.setVisible(true);
                statusText.setText("Opening file...");

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
            progressBar.setVisible(false);
            try {
                mainField.setText(loadFileTask.get());
                statusText.setText("Opened " + file.getName());
                currentLoadedFile = file;
            } catch (InterruptedException | ExecutionException e) {
                statusText.setText("Error opening file");
            }
        });
        loadFileTask.setOnFailed(workerStateEvent -> {
            progressBar.setVisible(false);
            statusText.setText("Error opening file");
        });
        progressBar.progressProperty().bind(loadFileTask.progressProperty());
        loadFileTask.run();
    }

    private void doSaveFile(File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(mainField.getText());
        fileWriter.close();
        statusText.setText("File saved to " + file.getName());
    }

    private void doSaveAs() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            statusText.setText("Invalid save file provided");
            return;
        }

        currentLoadedFile = file;
        doSaveFile(file);
    }
}