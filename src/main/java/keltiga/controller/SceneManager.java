package keltiga.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import keltiga.model.User;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;


    public static void setStage(Stage stage) {
        primaryStage = stage;
    }


    public static void switchToUserSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/UserSelection.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading /view/UserSelection.fxml");
        }
    }


    public static void switchToLeaderboard(User currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/Leaderboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);


            LeaderboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading /view/Leaderboard.fxml");
        }
    }

    public static void switchToLevelSelection() {
        try {
            System.out.println("Switching to Level Selection...");
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/GameLayout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading /view/GameLayout.fxml");
        }
    }
}
