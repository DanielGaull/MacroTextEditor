package com.danieljgaull.texteditor.texteditor;

import com.danieljgaull.texteditor.texteditor.instruction.Instruction;
import com.danieljgaull.texteditor.texteditor.instruction.InstructionParser;
import com.danieljgaull.texteditor.texteditor.util.PrimaryStageAware;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("text-view.fxml"));
        // https://stackoverflow.com/questions/26494865/javafx-8-changing-title-of-primary-stage
        fxmlLoader.setControllerFactory((Class<?> type) -> {
            try {
                Object controller = type.newInstance();
                if (controller instanceof PrimaryStageAware) {
                    ((PrimaryStageAware) controller).setPrimaryStage(stage);
                }
                return controller;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Text Editor");
        stage.setScene(scene);
        stage.show();

        InstructionParser parser = new InstructionParser();
        Instruction result = parser.parse("insert (\"-\" * size) (\"-\" * size)");
        System.out.println();
    }

    public static void main(String[] args) {
        launch();
    }
}