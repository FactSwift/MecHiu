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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameController {

    @FXML
    private TextField inputField;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label healthLabel;
    @FXML
    private Pane gamePane;
    @FXML
    private Button startButton;

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

    // Map to store word creation times
    private Map<String, Long> wordCreationTimes = new ConcurrentHashMap<>();

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
            Map<String, List<String>> wordPools = gson.fromJson(reader, Map.class);

            if (wordPools != null && wordPools.containsKey(difficulty.toLowerCase())) {
                wordPool = wordPools.get(difficulty.toLowerCase());
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
        wordText.setLayoutX(random.nextInt((int) gamePane.getWidth() - 100));
        wordText.setLayoutY(0);

        // Record the creation time of the word
        wordCreationTimes.put(word, System.currentTimeMillis());

        gamePane.getChildren().add(wordText);

        Timeline fall = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            wordText.setLayoutY(wordText.getLayoutY() + 5);

            if (wordText.getLayoutY() >= gamePane.getHeight()) {
                gamePane.getChildren().remove(wordText);
                wordMissed(word);
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
    }

    private void wordMissed(String word) {
        health--;
        updateUI();

        // Remove the word from the map to avoid memory leaks
        wordCreationTimes.remove(word);

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

                // Calculate the score based on the time taken
                Long creationTime = wordCreationTimes.remove(wordText.getText());
                if (creationTime != null) {
                    long elapsedTime = System.currentTimeMillis() - creationTime;

                    // Scale the score: 100 - (elapsed time in seconds), minimum 1
                    int wordScore = Math.max(1, 100 - (int) (elapsedTime / 100));
                    score += wordScore;
                }

                updateUI();
                return;
            }
        }
    }

    @FXML
    public void endGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);
        }
        SceneManager.switchToLeaderboard();
    }
}
