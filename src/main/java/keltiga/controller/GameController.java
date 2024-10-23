package keltiga.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import keltiga.dao.UserDAO;
import keltiga.model.User;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameController {

    @FXML private TextField inputField;
    @FXML private Label scoreLabel;
    @FXML private Label healthLabel;
    @FXML private Pane gamePane;
    @FXML private Button startButton;

    private int score = 0;
    private int health = 3;
    private String selectedIsland;
    private String selectedDifficulty;
    private Timeline gameLoop;
    private Random random = new Random();
    private List<String> wordPool;
    private User currentUser;
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    @FXML
    public void initialize() {
        this.currentUser = SceneManager.getCurrentUser();
        inputField.setOnAction(event -> checkInput());
    }


    @FXML
    public void selectIslandCoral() {
        selectedIsland = "Coral Island";
        System.out.println("Coral Island selected.");
    }


    @FXML
    public void selectIslandReef() {
        selectedIsland = "Reef Island";
        System.out.println("Reef Island selected.");
    }

    @FXML
    public void selectIslandDeepSea() {
        selectedIsland = "Deep Sea Island";
        System.out.println("Deep Sea Island selected.");
    }

    @FXML
    private void selectDifficultyEasy() {
        selectedDifficulty = "easy";
        System.out.println("Easy");
    }

    @FXML
    private void selectDifficultyMedium() {
        selectedDifficulty = "medium";
        System.out.println("Medium");
    }

    @FXML
    private void selectDifficultyHard() {
        selectedDifficulty = "hard";
        System.out.println("Hard");
    }

    private void updateUI() {
        if (healthLabel != null) {
            healthLabel.setText("Health: " + health);
        }
        scoreLabel.setText("Score: " + score);
        healthLabel.setText("Health: " + health);
    }


    public void startGame(String island, String difficulty) {
        loadWordPool(island, difficulty);
        score = 0;
        health = 3;
        updateUI();

        gameLoop = new Timeline(new KeyFrame(Duration.millis(2000), e -> spawnWord()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }


    private void loadWordPool(String island, String difficulty) {
        try (FileReader reader = new FileReader("data/" + island + ".json")) {
            Gson gson = new Gson();
            // Ensure the wordPool map is loaded based on the difficulty level
            Map<String, List<String>> wordPools = gson.fromJson(reader, Map.class);

            System.out.println("Loaded word pool from JSON: " + wordPools);

            // Check if the word pool exists for the selected difficulty
            if (wordPools != null && wordPools.containsKey(difficulty.toLowerCase())) {
                wordPool = wordPools.get(difficulty.toLowerCase());
                System.out.println("Words for " + difficulty + " difficulty: " + wordPool);
            } else {
                System.out.println("No words available for the selected difficulty: " + difficulty);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading word pool from JSON file.");
        }
    }



    private void spawnWord() {
        if (wordPool == null || wordPool.isEmpty()) {
            System.out.println("Word pool is empty. No words to spawn.");
            return;
        }

        String word = wordPool.get(random.nextInt(wordPool.size()));
        Text wordText = new Text(word);
        wordText.setLayoutX(random.nextInt((int) gamePane.getWidth() - 100));  // Random X position
        wordText.setLayoutY(0);  // Start at the top of the pane

        gamePane.getChildren().add(wordText);

        // Make the word fall down the screen
        Timeline fall = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            wordText.setLayoutY(wordText.getLayoutY() + 5);  // Move word down by 5 pixels
            if (wordText.getLayoutY() > gamePane.getHeight()) {
                gamePane.getChildren().remove(wordText);
                wordMissed();  // Trigger when word reaches the bottom
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
    }

    private void wordMissed() {
        health--;
        updateUI();
        if (health <= 0) {
            endGame();
        }
    }

    private void checkInput() {
        String typedWord = inputField.getText().trim();
        inputField.clear();


        for (Text wordText : gamePane.getChildren().filtered(node -> node instanceof Text).toArray(Text[]::new)) {
            if (wordText.getText().equalsIgnoreCase(typedWord)) {
                gamePane.getChildren().remove(wordText);
                score++;
                updateUI();
                return;
            }
        }
    }



    @FXML
    public void endGame() {
        if (gameLoop != null) {
            gameLoop.stop();  // Stop the game loop
        }
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);  // Save updated score
        }
        // Switch to the leaderboard or any other screen you want
        SceneManager.switchToLeaderboard();  // Assuming you switch to the leaderboard after ending the game
    }
}
