package keltiga;

import javafx.application.Application;
import javafx.stage.Stage;
import keltiga.controller.SceneManager;

public class MechiuApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setStage(primaryStage);
        SceneManager.switchToUserSelection();
        primaryStage.setTitle("Typer Game - Mechiu");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
