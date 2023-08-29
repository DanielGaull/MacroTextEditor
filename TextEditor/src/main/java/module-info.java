module com.danieljgaull.texteditor.texteditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.danieljgaull.texteditor.texteditor to javafx.fxml;
    exports com.danieljgaull.texteditor.texteditor;
    exports com.danieljgaull.texteditor.texteditor.handlers;
    opens com.danieljgaull.texteditor.texteditor.handlers to javafx.fxml;
}