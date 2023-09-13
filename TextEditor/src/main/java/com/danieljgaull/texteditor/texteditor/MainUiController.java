package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.plugin.Plugin;
import com.danieljgaull.texteditor.texteditor.plugin.PluginLoader;
import com.danieljgaull.texteditor.texteditor.text.TextChange;
import com.danieljgaull.texteditor.texteditor.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainUiController implements PrimaryStageAware {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField macroField;
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

        // Need to load in plugins
        PluginLoader pluginLoader = new PluginLoader(System.getenv("APPDATA") + File.separator + "TextEditor");
        List<Plugin> plugins = new ArrayList<>();
        List<ObjectOrError<Plugin>> loadedPlugins = new ArrayList<>();
        try {
            loadedPlugins = pluginLoader.loadPlugins();
        } catch (FileNotFoundException e) {
            // TODO: Proper UI error handling for this and for individual plugin load errors
            System.out.println("Plugin file is missing! No plugins have been loaded");
        }
        for (int i = 0; i < loadedPlugins.size(); i++) {
            ObjectOrError<Plugin> attempt = loadedPlugins.get(i);
            if (attempt.isError()) {
                System.out.println("Failed to load plugin '" + attempt.getName() + "'. Message: " +
                        attempt.getErrorMessage());
            } else {
                plugins.add(attempt.getObject());
            }
        }

        textEditorController = new TextEditorController(
                str -> statusText.setText(str),
                str -> {
                    if (stage != null) {
                        stage.setTitle(str);
                    }
                },
                str -> modeText.setText("Mode: " + str),
                plugins
        );

        textArea.setTextFormatter(new TextFormatter<String>(this::handleTextChange));
        textArea.setFont(Font.font("Consolas", FontWeight.NORMAL, 13));

        macroField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    runMacro(macroField.getText());
                }
            }
        });

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

    private void startMacro() {
//        Point caretPos = calculateCaretPosition();
//        macroField.setLayoutX(caretPos.x);
//        macroField.setLayoutY(caretPos.y);
//        macroField.setVisible(true);
        macroField.setText("");
        macroField.requestFocus();
    }

    private void runMacro(String text) {
        int caret = textArea.getCaretPosition();
        String currentText = textArea.getText();
        int linePosition = getLineForPosition(currentText, caret);
        int startOfLineIndex = StringUtils.lastIndexOfChar(currentText, caret, '\n') + 1;
        int lineCaretPos = caret - startOfLineIndex - 1;
        if (lineCaretPos < 0) lineCaretPos = 0;

        textEditorController.runMacro(text, linePosition, lineCaretPos);

        // Make sure we've properly set the text in here
        String fullText = textEditorController.buildText();
        muteTextFormatter = true; // Don't trigger ourselves again with this change
        textArea.setText(fullText);
        muteTextFormatter = false;

        // TODO: make sure we have these set properly
        textArea.selectRange(textEditorController.getCaret(), textEditorController.getAnchor());

        textArea.requestFocus();
        macroField.setText("");
    }

    private Point calculateCaretPosition() {
        String text = textArea.getText();
        int caretPosition = text.length();//textArea.getCaretPosition();
        int startOfLineIndex = StringUtils.lastIndexOfChar(text, caretPosition, '\n') + 1;

        Font font = textArea.getFont();

        // Create a temp Text node to calculate the positioning
        Text tempForX = new Text(text.substring(startOfLineIndex, caretPosition));
        Text tempForY = new Text(text.substring(0, startOfLineIndex));
        tempForX.setFont(font);
        tempForY.setFont(font);
        tempForX.setBoundsType(TextBoundsType.VISUAL);
        tempForY.setBoundsType(TextBoundsType.VISUAL);
        double x = tempForX.getLayoutBounds().getWidth();
        double y = tempForY.getLayoutBounds().getHeight();

        return new Point(x, y);
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

    private TextFormatter.Change handleTextChange(TextFormatter.Change change) {
        if (muteTextFormatter) {
            return change;
        }
        if (!change.isContentChange()) {
            // We don't need to handle it here
            // Let the change pass through unhindered
            return change;
        }

        String currentText = textArea.getText();
        int caret = change.getCaretPosition();
        int anchor = change.getAnchor();
        int caretMinusText = caret - change.getText().length();
        int linePosition = getLineForPosition(currentText, caretMinusText);
        // TODO: Remember to handle prefix/suffix text properly with this
        int startOfLineIndex = StringUtils.lastIndexOfChar(currentText, caretMinusText, '\n') + 1;
        TextChange textChange = null;
        if (change.getText().equals("\n")) {
            textChange = new TextChange().newLine();
        } else if (change.getText().equals("\\")) {
            // TODO: Make it so pasting in \ works
            // Make sure this change isn't registered
            textChange = new TextChange();
            // Make sure that we subtract one to account for the \ that it believes was typed
            caret--;
            anchor--;
            startMacro();
        } else {
            if (change.getText().contains("\n")) {
                // Split on the newline
                String[] typed = change.getText().split("\n");
                List<String> newLines = new ArrayList<>(Arrays.asList(typed));
                textChange = new TextChange().type(newLines);
            } else {
                // Simple to make it just type some text
                textChange = new TextChange().type(change.getText());
            }
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
        int lineCaretPos = caret - startOfLineIndex - change.getText().length();
        textEditorController.handleTextChange(textChange, lineCaretPos, linePosition);
        String fullText = textEditorController.buildText();
        textEditorController.setCaretAndAnchor(caret, anchor);
        muteTextFormatter = true; // Don't trigger ourselves again with this change
        textArea.setText(fullText);
        muteTextFormatter = false;

        // Change the change to fit
        // TODO: Use a constructor to build the change we want to see
        change.setText("");
        change.setRange(0, 0);
        return change;
    }
}