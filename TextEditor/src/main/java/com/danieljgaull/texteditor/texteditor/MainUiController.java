package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.text.TextChange;
import com.danieljgaull.texteditor.texteditor.util.PrimaryStageAware;
import com.danieljgaull.texteditor.texteditor.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class MainUiController implements PrimaryStageAware {
    @FXML
    private TextArea textArea;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusText;
    @FXML
    private Label modeText;

    private Stage stage;

    private TextEditorController textEditorController;

    public void initialize() {
        // Only set the progress bar visible when stuff is loading
        progressBar.setVisible(false);

        textEditorController = new TextEditorController(
                str -> statusText.setText(str),
                str -> {
                    if (stage != null) {
                        stage.setTitle(str);
                    }
                },
                str -> modeText.setText("Mode: " + str)
        );

        textArea.setTextFormatter(new TextFormatter<String>(change -> {
            int linePosition = StringUtils.countChar(textArea.getText(), '\n');
            TextChange textChange = null;
            if (change.getText().equals("\n")) {
                textChange = TextChange.newLine(linePosition);
            } else {
                textChange = TextChange.typeText(change.getText(), change.getCaretPosition(), linePosition);
            }
            TextChange result = onTextChange(textChange);
            return change;//onTextChange(change);
        }));
        textArea.setFont(Font.font("Consolas", FontWeight.NORMAL, 13));

        KeyCodeInitializer keyCodeInitializer = new KeyCodeInitializer();

        textArea.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                keyCodeInitializer.initialize(textArea.getScene(), this);
            }
        });
        textArea.setWrapText(true);
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

    public TextChange onTextChange(TextChange change) {
        textEditorController.makeDirty();

        // TODO: Have the TextEditorController handle the change, then come back to us
        // NOTE: Text Editor Controller should determine if it should become dirty, because change may be ignored here

        return null;
    }

    /*
     * Helper Methods
     */
    public void save() {
        textEditorController.save(textArea.getText());
    }

    public void saveAs() {
        textEditorController.saveAs(textArea.getText());
    }

    public void open() {
        textEditorController.open(txt -> textArea.setText(txt),
                () -> progressBar.setVisible(true), () -> progressBar.setVisible(false),
                () -> progressBar.setVisible(false),
                List.of(progressBar.progressProperty()));
    }

    @Override
    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }
}