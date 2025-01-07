package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class GameOverController {
    @FXML
    private Text finalScoreText;
    @FXML
    private Text islandText;
    @FXML
    private Text difficultyText;
    
    private String currentIsland;
    private String currentDifficulty;
    private MediaPlayer backgroundMusic;
    
    @FXML
    public void initialize() {
        System.out.println("GameOverController initialized");
        // Add fade-in animation when scene loads
        if (finalScoreText != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), finalScoreText);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } else {
            System.err.println("finalScoreText is null in initialize()");
        }

        // Play game over music when scene initializes
        playGameOverMusic();
    }
    
    public void setGameData(String island, String difficulty, int score) {
        System.out.println("Setting game data - Island: " + island + ", Difficulty: " + difficulty + ", Score: " + score);
        this.currentIsland = island;
        this.currentDifficulty = difficulty;
        
        if (finalScoreText != null) {
            finalScoreText.setText("Final Score: " + score);
        }
        
        if (islandText != null) {
            islandText.setText("Island: " + island);
        }
        
        if (difficultyText != null) {
            difficultyText.setText("Difficulty: " + difficulty);
        }
    }
    
    @FXML
    private void onNextButtonClick() {
        stopCurrentMusic();
        SceneManager.startGame(currentIsland, currentDifficulty);
    }
    
    @FXML
    private void onHomeButtonClick() {
        stopCurrentMusic();
        SceneManager.switchToHome();
    }

    private void playGameOverMusic() {
        try {
            URL musicFile = getClass().getResource("/music/kalah.mp3");
            if (musicFile != null) {
                Media sound = new Media(musicFile.toString());
                backgroundMusic = new MediaPlayer(sound);
                backgroundMusic.setVolume(0.8);
                backgroundMusic.setCycleCount(1);
                backgroundMusic.play();
            } else {
                System.err.println("Could not find music file: /music/kalah.mp3");
            }
        } catch (Exception e) {
            System.err.println("Error playing game over music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void stopCurrentMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }
}