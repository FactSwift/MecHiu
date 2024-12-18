package keltiga.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import keltiga.model.User;

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

    static void switchToMapIsland() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/MapIsland.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Switch to the level selection screen
    public static void switchToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/Home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
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
            primaryStage.show();
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

    public static void switchToInfographic(String island, String difficulty, int score) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/Infographic.fxml"));
            Parent root = loader.load();
            
            InfographicController controller = loader.getController();
            controller.initialize(island, difficulty, score);
            
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
