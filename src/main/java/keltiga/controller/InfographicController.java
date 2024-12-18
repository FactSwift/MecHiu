package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import keltiga.model.User;
import keltiga.dao.UserDAO;

public class InfographicController {
    @FXML
    private ImageView infographicImage;
    
    private String selectedIsland;
    private String selectedDifficulty;
    private int finalScore;
    private MediaPlayer backgroundMusic;
    
    public void initialize(String island, String difficulty, int score) {
        this.selectedIsland = island;
        this.selectedDifficulty = difficulty;
        this.finalScore = score;
        
        String imagePath = getInfographicPath(island, difficulty);
        infographicImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        
        User currentUser = SceneManager.getCurrentUser();
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            UserDAO userDAO = new UserDAO();
            userDAO.saveUser(currentUser);
        }
        
        playWinMusic();
    }
    
    private void playWinMusic() {
        try {
            String musicFile = "/music/menang.mp3";
            System.out.println("Attempting to play win music from: " + musicFile);
            
            Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setVolume(1.0);
            backgroundMusic.play();
            
            System.out.println("Win music started playing");
        } catch (Exception e) {
            System.out.println("Error playing win music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void stopMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
                backgroundMusic.dispose();
            } catch (Exception e) {
                System.out.println("Error stopping music: " + e.getMessage());
            }
        }
    }
    
    private String getInfographicPath(String island, String difficulty) {
        String basePath = "/view/Image/";
        
        if (island.equals("Sumatra Island")) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return basePath + "artikel1.png";
                case "medium":
                    return basePath + "artikel2.png";
                case "hard":
                    return basePath + "artikel3.png";
            }
        } else if (island.equals("Java Island")) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return basePath + "artikel4.png";
                case "medium":
                    return basePath + "artikel5.png";
                case "hard":
                    return basePath + "artikel6.png";
            }
        } else if (island.equals("Papua Island")) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return basePath + "artikel7.png";
                case "medium":
                    return basePath + "artikel8.png";
                case "hard":
                    return basePath + "artikel9.png";
            }
        }
        
        return basePath + "artikel1.png";
    }
    
    @FXML
    private void continueToLeaderboard() {
        stopMusic();
        SceneManager.switchToLeaderboard();
    }
} 