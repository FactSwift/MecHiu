package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import keltiga.model.User;
import java.util.Map;
import java.net.URL;

public class LevelSelectionController {
 
    @FXML private Button sumatraIslandButton;
    @FXML private Button javaIslandButton;
    @FXML private Button papuaIslandButton;
    @FXML private Label userLabel;
    
    private String selectedIsland = "";
    private User currentUser;
    private MediaPlayer backgroundMusic;

    @FXML
    public void initialize() {
        SceneManager.setLevelSelectionController(this);
        try {
            currentUser = SceneManager.getCurrentUser();
            
            // Set username
            if (userLabel != null && currentUser != null) {
                userLabel.setText(currentUser.getUsername());
            }
            
            // Initialize unlockedIslands jika null
            if (currentUser != null && currentUser.getUnlockedIslands() == null) {
                currentUser.updateIslandProgress("Sumatra Island", "none");
            }
            
            // Set island buttons state
            if (currentUser != null) {
                // Sumatra selalu terbuka
                if (sumatraIslandButton != null) {
                    sumatraIslandButton.setDisable(false);
                    sumatraIslandButton.setOpacity(1.0);
                }
                
                // Java dan Papua berdasarkan progress
                if (javaIslandButton != null) {
                    boolean javaUnlocked = currentUser.isIslandUnlocked("Java Island");
                    javaIslandButton.setDisable(!javaUnlocked);
                    javaIslandButton.setOpacity(javaUnlocked ? 1.0 : 0.5);
                }
                
                if (papuaIslandButton != null) {
                    boolean papuaUnlocked = currentUser.isIslandUnlocked("Papua Island");
                    papuaIslandButton.setDisable(!papuaUnlocked);
                    papuaIslandButton.setOpacity(papuaUnlocked ? 1.0 : 0.5);
                }
            }
            
            playBackgroundMusic();
            
        } catch (Exception e) {
            System.out.println("Error in initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playBackgroundMusic() {
        try {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.dispose();
            }
            
            URL musicUrl = getClass().getResource("/music/island.mp3");
            if (musicUrl == null) {
                System.out.println("Music file not found at: /music/island.mp3");
                return;
            }
            
            Media sound = new Media(musicUrl.toExternalForm());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setVolume(0.5);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.play();
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        try {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.dispose();
                backgroundMusic = null;
            }
        } catch (Exception e) {
            System.out.println("Error stopping music: " + e.getMessage());
        }
    }

    @FXML
    private void selectSumatraIsland() {
        stopBackgroundMusic();
        showDifficultyDialog("Sumatra Island");
    }

    @FXML
    private void selectJavaIsland() {
        if (!currentUser.isIslandUnlocked("Java Island")) return;
        stopBackgroundMusic();
        showDifficultyDialog("Java Island");
    }

    @FXML
    private void selectPapuaIsland() {
        if (!currentUser.isIslandUnlocked("Papua Island")) return;
        stopBackgroundMusic();
        showDifficultyDialog("Papua Island");
    }

    private void showDifficultyDialog(String island) {
        try {
            if (currentUser == null) return;
            
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Select Difficulty");
            
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #36AEC6; -fx-background-radius: 15px;");
            
            VBox buttonBox = new VBox(15);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new javafx.geometry.Insets(25));
            
            Button easyBtn = new Button("EASY");
            Button mediumBtn = new Button("MEDIUM");
            Button hardBtn = new Button("HARD");
            
            String baseStyle = "-fx-font-size: 16px; -fx-min-width: 200; -fx-text-fill: white;";
            easyBtn.setStyle(baseStyle + "-fx-background-color: #4CAF50;");
            mediumBtn.setStyle(baseStyle + "-fx-background-color: #FFA000;");
            hardBtn.setStyle(baseStyle + "-fx-background-color: #D32F2F;");
            
            easyBtn.setDisable(!currentUser.canPlayDifficulty(island, "easy"));
            mediumBtn.setDisable(!currentUser.canPlayDifficulty(island, "medium"));
            hardBtn.setDisable(!currentUser.canPlayDifficulty(island, "hard"));
            
            buttonBox.getChildren().addAll(easyBtn, mediumBtn, hardBtn);
            dialogPane.setContent(buttonBox);
            dialogPane.getButtonTypes().add(ButtonType.CANCEL);
            Node closeButton = dialogPane.lookupButton(ButtonType.CANCEL);
            closeButton.setVisible(false);
            
            easyBtn.setOnAction(e -> {
                dialog.close();
                stopBackgroundMusic();
                SceneManager.startGame(island, "easy");
            });
            
            mediumBtn.setOnAction(e -> {
                dialog.close();
                stopBackgroundMusic();
                SceneManager.startGame(island, "medium");
            });
            
            hardBtn.setOnAction(e -> {
                dialog.close();
                stopBackgroundMusic();
                SceneManager.startGame(island, "hard");
            });
            
            dialog.setOnCloseRequest(e -> {
                // Do nothing
            });
            
            dialog.showAndWait();
        } catch (Exception e) {
            System.out.println("Error showing difficulty dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void switchUser() {
        stopBackgroundMusic();
        SceneManager.switchToUserSelection();
    }

    @FXML
    private void goToMap() {
        stopBackgroundMusic();
        SceneManager.switchToMapIsland();
    }

    public void onSceneChange() {
        stopBackgroundMusic();
    }
}