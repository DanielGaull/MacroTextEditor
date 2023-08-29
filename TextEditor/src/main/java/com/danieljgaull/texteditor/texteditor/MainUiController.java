package com.danieljgaull.texteditor.texteditor;

import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class MainUiController {
    @FXML
    private TextArea mainField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusText;

    private TextEditorController textEditorController;

    private File currentLoadedFile;

    public void initialize() {
        // Only set the progress bar visible when stuff is loading
        progressBar.setVisible(false);

        textEditorController = new TextEditorController(str -> statusText.setText(str));
    }

    /*
     * Bound methods
     */

    public void openFile(ActionEvent event) {
        textEditorController.open(txt -> mainField.setText(txt),
                () -> progressBar.setVisible(true), () -> progressBar.setVisible(false),
                () -> progressBar.setVisible(false),
                List.of(progressBar.progressProperty()));
    }
    public void saveAs(ActionEvent event) {
        textEditorController.saveAs(mainField.getText());
    }
    public void save(ActionEvent event) {
        textEditorController.save(mainField.getText());
    }

    /*
     * Helper Methods
     */




}