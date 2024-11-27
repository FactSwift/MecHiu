package keltiga.controller;

import javafx.scene.text.TextFlow;
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
import java.util.HashMap;
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

    // Map to store words and their spawn times
    private Map<TextFlow, Long> wordSpawnTimes = new HashMap<TextFlow, Long>();

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
            Map<String, List<String>> wordPools = gson.fromJson(reader, Map.class);

            System.out.println("Loaded word pool from JSON: " + wordPools);

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
        wordText.setStyle("-fx-font-size: 16px; -fx-fill: black;"); // Set font size and text color

        // Wrap the text in a TextFlow for background support
        TextFlow wordWrapper = new TextFlow(wordText);
        wordWrapper.setStyle("-fx-background-color: white; -fx-padding: 6px; -fx-border-color: black; -fx-border-radius: 5px;");
        wordWrapper.setLayoutX(random.nextInt((int) gamePane.getWidth() - 100)); // Random X position
        wordWrapper.setLayoutY(0); // Start at the top of the pane

        gamePane.getChildren().add(wordWrapper);
        wordSpawnTimes.put(wordWrapper, System.currentTimeMillis()); // Store spawn time

        Timeline fall = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            wordWrapper.setLayoutY(wordWrapper.getLayoutY() + 5); // Move word down by 5 pixels
            if (wordWrapper.getLayoutY() > gamePane.getHeight()) {
                if (gamePane.getChildren().contains(wordWrapper)) { // Avoid multiple processing
                    gamePane.getChildren().remove(wordWrapper);
                    wordSpawnTimes.remove(wordWrapper);
                    wordMissed();
                }
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

        // Iterasi untuk menemukan kata yang cocok
        for (javafx.scene.Node node : gamePane.getChildren()) {
            if (node instanceof TextFlow) {
                TextFlow textFlow = (TextFlow) node;
                Text wordText = (Text) textFlow.getChildren().get(0); // Ambil teks dari TextFlow

                if (wordText.getText().equalsIgnoreCase(typedWord)) {
                    gamePane.getChildren().remove(textFlow); // Hapus seluruh TextFlow
                    Long spawnTime = wordSpawnTimes.remove(textFlow); // Ambil waktu spawn
                    if (spawnTime != null) {
                        calculateScore(spawnTime); // Hitung skor
                    }
                    updateUI();
                    return;
                }
            }
        }
    }


    private void calculateScore(long spawnTime) {
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - spawnTime;  // Time in milliseconds
        int maxDuration = 5000;  // Maximum duration for scoring (e.g., 5 seconds)

        // Calculate score (1 to 100)
        int wordScore = Math.max(1, 100 - (int) ((duration / (double) maxDuration) * 99));
        score += wordScore;

        System.out.println("Word typed in " + duration + "ms, score: " + wordScore);
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
