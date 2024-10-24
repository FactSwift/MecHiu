package keltiga.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import keltiga.model.User;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;
    private static User currentUser;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // Switch to the level selection screen
    public static void switchToLevelSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/LevelSelection.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Switch to the leaderboard screen
    public static void switchToLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/Leaderboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Switch to user selection screen
    public static void switchToUserSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/UserSelection.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startGame(String island, String difficulty) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/GameLayout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);


            GameController controller = loader.getController();
            controller.startGame(island, difficulty);

            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
