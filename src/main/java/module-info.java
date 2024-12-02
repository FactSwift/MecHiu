module keltiga.mechiu {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;

    opens keltiga to javafx.fxml;   // Open package to JavaFX FXML loader
    opens keltiga.model to com.google.gson;  // Open model package to Gson

    exports keltiga;
    exports keltiga.model;
    opens keltiga.controller to javafx.fxml;  // Open controller package to FXML
}
