package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.handlers.Action;
import com.danieljgaull.texteditor.texteditor.util.Tuple;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyCodeInitializer {

    public KeyCodeInitializer() {
    }

    public void initialize(Scene scene, MainUiController controller) {
        List<Tuple<KeyCombination, Action>> keyCombs = getKeyCombinations(controller);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            for (Tuple<KeyCombination, Action> comb : keyCombs) {
                if (comb.first().match(keyEvent)) {
                    comb.second().call();
                    keyEvent.consume();
                    break;
                }
            }
        });
    }

    private List<Tuple<KeyCombination, Action>> getKeyCombinations(MainUiController controller) {
        return Arrays.asList(
                new Tuple<>(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                        controller::save),
                new Tuple<>(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
                        controller::saveAs),
                new Tuple<>(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN),
                        controller::open)
        );
    }
}
