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

    private boolean muteTextFormatter = false;

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
            if (muteTextFormatter) {
                return change;
            }
            if (!change.isContentChange()) {
                // We don't need to handle it here
                // Let the change pass through unhindered
                return change;
            }

            int linePosition = StringUtils.countChar(textArea.getText(),
                    change.getCaretPosition() - change.getText().length(), '\n');
            // TODO: Remember to handle prefix/suffix text properly with this
            // TODO: Handle text deletion
            int startOfLineIndex = StringUtils.lastIndexOfChar(textArea.getText(),
                    change.getCaretPosition() - change.getText().length(), '\n') + 1;
            TextChange textChange = null;
            if (change.getText().equals("\n")) {
                textChange = TextChange.newLine();
            } else {
                textChange = TextChange.typeText(change.getText());
            }
            int lineCaretPos = change.getCaretPosition() - startOfLineIndex - change.getText().length();
            textEditorController.handleTextChange(textChange, lineCaretPos, linePosition);
            String fullText = textEditorController.buildText();
            muteTextFormatter = true; // Don't trigger ourselves again with this change
            textArea.setText(fullText);
            muteTextFormatter = false;

            // Change the change to fit
            // TODO: Use a constructor to build the change we want to see
            change.setText("");
            change.setRange(change.getCaretPosition(), change.getCaretPosition());
            return change;
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