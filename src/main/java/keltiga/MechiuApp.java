package keltiga;

import javafx.application.Application;
import javafx.stage.Stage;
import keltiga.controller.SceneManager;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;

public class MechiuApp extends Application {
    private static PrintWriter logFile;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Setup logging
            logFile = new PrintWriter(new FileWriter("mechiu-debug.log", true));
            System.setOut(new PrintStream(new FileOutputStream("mechiu-debug.log", true)));

            SceneManager.initialize(primaryStage);
            SceneManager.switchToUserSelection();
            primaryStage.setTitle("Typer Game - Mechiu");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (logFile != null) {
            logFile.close();
        }
    }
}
