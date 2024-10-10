module org.keltiga.mechiu {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens org.keltiga.mechiu to javafx.fxml;
    exports org.keltiga.mechiu;
    exports org.keltiga.mechiu.models;
    opens org.keltiga.mechiu.models to javafx.fxml;
}