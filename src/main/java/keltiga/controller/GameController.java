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
    public void selectIslandJava() {
        selectedIsland = "Java Island";
        System.out.println("Java Island selected.");
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


    public void startGame(String island, String difficulty) {
        loadWordPool(island, difficulty);
        score = 0;
        health = 3;
        updateUI();

        // Game loop for spawning words at a slower pace (words per devtik)
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(4), e -> spawnWord()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }


    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        healthLabel.setText("Health: " + health);
    }

    // Load word pool from JSON file
    private void loadWordPool(String island, String difficulty) {
        try (FileReader reader = new FileReader("data/" + island + ".json")) {
            // Ensure the wordPool map is loaded based on the difficulty level
            Map<String, List<String>> wordPools = gson.fromJson(reader, Map.class);

            System.out.println("Loaded word pool from JSON: " + wordPools);

            // Debugger
            if (wordPools != null && wordPools.containsKey(difficulty.toLowerCase())) {
                wordPool = wordPools.get(difficulty.toLowerCase());
                System.out.println("Words for " + difficulty + " difficulty: " + wordPool);
            } else {
                System.out.println("No words available for the selected difficulty: " + difficulty);
                wordPool = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading word pool from JSON file.");
        }
    }

    // Word spawner
    private void spawnWord() {
        if (wordPool == null || wordPool.isEmpty()) {
            System.out.println("Word pool is empty. No words to spawn.");
            return;
        }

        String word = wordPool.get(random.nextInt(wordPool.size()));
        Text wordText = new Text(word);
        wordText.setLayoutX(random.nextInt((int) gamePane.getWidth() - 100));  // Random position
        wordText.setLayoutY(0);  // Start at the top

        gamePane.getChildren().add(wordText);

        // Word falling animation (move slower to avoid health being depleted too fast)
        Timeline fall = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            wordText.setLayoutY(wordText.getLayoutY() + 2);  // Move word down by 2px per frame
            if (wordText.getLayoutY() > gamePane.getHeight()) {
                gamePane.getChildren().remove(wordText);
                wordMissed();  // If word reaches the bottom
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
    }

    // Handle when a word is missed
    private void wordMissed() {
        health--;
        updateUI();
        if (health <= 0) {
            endGame();
        }
    }

    // Check input field and compare it with the falling word
    private void checkInput() {
        String typedWord = inputField.getText().trim();
        inputField.clear();

        // Check if the typed word matches any falling word
        for (Text wordText : gamePane.getChildren().filtered(node -> node instanceof Text).toArray(Text[]::new)) {
            if (wordText.getText().equalsIgnoreCase(typedWord)) {
                gamePane.getChildren().remove(wordText);  // Remove word if typed correctly
                score++;  // Increment score
                updateUI();
                return;
            }
        }
    }

    // End the game and save the score
    @FXML
    private void endGame() {
        if (gameLoop != null) {
            gameLoop.stop();  // Stop the game loop to avoid conflicts during scene switch
        }

        // Save high score if current score is higher
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);  // Save updated score
        }

        SceneManager.switchToLeaderboard();  // Show the leaderboard
    }


}
