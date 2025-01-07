package keltiga.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import keltiga.model.User;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;

public class SceneManager {

    private static Stage primaryStage;
    private static User currentUser;
    private static final double FIXED_WIDTH = 969.0;
    private static final double FIXED_HEIGHT = 546.0;
    private static LevelSelectionController levelSelectionController;

    public static void initialize(Stage stage) {
        primaryStage = stage;
        stage.setResizable(false);
        
        // Set ukuran scene dan stage yang tepat
        Scene scene = new Scene(new AnchorPane(), FIXED_WIDTH, FIXED_HEIGHT);
        stage.setScene(scene);
        stage.sizeToScene();
        
        // Center window setelah ukuran diatur
        stage.centerOnScreen();
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
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/Infographic.fxml"));
                    Parent root = loader.load();
                    
                    InfographicController controller = loader.getController();
                    controller.initialize(island, difficulty, score);
                    
                    Scene scene = new Scene(root);
                    primaryStage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback ke home jika terjadi error
            switchToHome();
        }
    }

    public static void switchToGameOver(String island, String difficulty, int score) {
        // Jangan inisialisasi GameOver jika game sedang dalam proses quit
        if (GameController.isQuitting() || GameController.isProcessingQuit()) {
            switchToHome();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/GameOver.fxml"));
            Parent root = loader.load();
            
            GameOverController controller = loader.getController();
            controller.setGameData(island, difficulty, score);
            
            Scene scene = new Scene(root, FIXED_WIDTH, FIXED_HEIGHT);
            
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Error switching to game over: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, FIXED_WIDTH, FIXED_HEIGHT);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLevelSelectionController(LevelSelectionController controller) {
        levelSelectionController = controller;
    }

    public static LevelSelectionController getLevelSelectionController() {
        return levelSelectionController;
    }

}
