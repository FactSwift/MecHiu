package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class InfographicController {
    @FXML
    private ImageView infographicImage;
    
    private String selectedIsland;
    private String selectedDifficulty;
    private int finalScore;
    
    public void initialize(String island, String difficulty, int score) {
        this.selectedIsland = island;
        this.selectedDifficulty = difficulty;
        this.finalScore = score;
        
        String imagePath = getInfographicPath(island, difficulty);
        infographicImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
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
        SceneManager.switchToLeaderboard();
    }
} 