package com.danieljgaull.texteditor.texteditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.util.List;

public class MainUiController {
    @FXML
    private TextArea mainField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusText;

    private TextEditorController textEditorController;

    public void initialize() {
        // Only set the progress bar visible when stuff is loading
        progressBar.setVisible(false);

        textEditorController = new TextEditorController(str -> statusText.setText(str));

        KeyCodeInitializer keyCodeInitializer = new KeyCodeInitializer();

        mainField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                keyCodeInitializer.initialize(mainField.getScene(), this);
            }
        });
    }

    /*
     * Bound methods
     */

    public void onOpenFile(ActionEvent event) {
        open();
    }
    public void onSaveAs(ActionEvent event) {
        saveAs();
    }
    public void onSave(ActionEvent event) {
        save();
    }

    /*
     * Helper Methods
     */
    public void save() {
        textEditorController.save(mainField.getText());
    }

    public void saveAs() {
        textEditorController.saveAs(mainField.getText());
    }

    public void open() {
        textEditorController.open(txt -> mainField.setText(txt),
                () -> progressBar.setVisible(true), () -> progressBar.setVisible(false),
                () -> progressBar.setVisible(false),
                List.of(progressBar.progressProperty()));
    }

}