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

import java.util.ArrayList;
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

            String currentText = textArea.getText();
            int caretMinusText = change.getCaretPosition() - change.getText().length();
            int linePosition = getLineForPosition(currentText, caretMinusText);
            // TODO: Remember to handle prefix/suffix text properly with this
            int startOfLineIndex = StringUtils.lastIndexOfChar(currentText, caretMinusText, '\n') + 1;
            List<Integer> deletedLines = new ArrayList<>();
            TextChange textChange = null;
            if (change.getText().equals("\n")) {
                textChange = new TextChange().newLine();
            } else {
                textChange = new TextChange().type(change.getText());
                if (change.isDeleted()) {
                    // If there are any \n in the deleted range, we need to do a delete lines
                    // Otherwise, we can just delete within a single line
                    if (currentText.substring(change.getRangeStart(), change.getRangeEnd()).contains("\n")) {
                        // Doing a 'delete lines'
                        // Determine which lines to delete
                        // Get the line position for the start and end, and delete every line that is between those
                        int startLine = getLineForPosition(currentText, change.getRangeStart());
                        int endLine = getLineForPosition(currentText, change.getRangeEnd());
                        // Now, we need to determine the range start/end. i.e. the caret/anchor
                        // on the 2 lines that we end up keeping.
                        int indexOfStartOfFirst = StringUtils.lastIndexOfChar(currentText, change.getRangeStart(), '\n') + 1;
                        int indexOfStartOfLast = StringUtils.lastIndexOfChar(currentText, change.getRangeEnd(), '\n') + 1;
                        int start = change.getRangeStart() - indexOfStartOfFirst;
                        int end = change.getRangeEnd() - indexOfStartOfLast;
                        textChange.deleteLines(startLine, endLine, start, end);
                    } else {
                        // Deleting within a line
                        int start = change.getRangeStart() - startOfLineIndex;
                        int end = change.getRangeEnd() - startOfLineIndex;
                        textChange.delete(start, end);
                    }
                }
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

    private static int getLineForPosition(String text, int position) {
        return StringUtils.countChar(text, position, '\n');
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