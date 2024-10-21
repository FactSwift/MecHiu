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

import java.util.*;

public class GameController {

    @FXML private TextField inputField;
    @FXML private Label scoreLabel;
    @FXML private Pane gamePane;
    @FXML private Button startButton;
    @FXML private Label stageLabel;

    private int score = 0;
    private Timeline gameLoop;
    private List<String> wordPool;
    private Random random = new Random();

    private String selectedIsland = "Coral Island";
    private String selectedDifficulty = "Easy";

    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    public void setCurrentUser(User user) {    // Method to set the current user
        this.currentUser = user;
    }

    // Method to select Coral Island
    @FXML
    private void selectIslandCoral() {
        this.selectedIsland = "Coral Island";
        loadWordPool(selectedIsland, selectedDifficulty);
    }

    @FXML
    private void selectIslandReef() {
        this.selectedIsland = "Reef Island";
        loadWordPool(selectedIsland, selectedDifficulty);
    }

    @FXML
    private void selectIslandDeepSea() {
        this.selectedIsland = "Deep Sea Island";
        loadWordPool(selectedIsland, selectedDifficulty);
    }

    @FXML
    private void selectDifficultyEasy() {
        this.selectedDifficulty = "Easy";
        loadWordPool(selectedIsland, selectedDifficulty);
    }

    @FXML
    private void selectDifficultyMedium() {
        this.selectedDifficulty = "Medium";
        loadWordPool(selectedIsland, selectedDifficulty);
    }

    @FXML
    private void selectDifficultyHard() {
        this.selectedDifficulty = "Hard";
        loadWordPool(selectedIsland, selectedDifficulty);
    }

    public void initialize() {

        loadWordPool(selectedIsland, selectedDifficulty);


        startButton.setOnAction(event -> startGame());


        inputField.setOnAction(event -> checkInput());
    }


    private void loadWordPool(String island, String difficulty) {

        Map<String, List<String>> wordPools = new HashMap<>();

        if (island.equals("Coral Island")) {
            if (difficulty.equals("Easy")) {
                wordPools.put("Easy", Arrays.asList("cat", "dog", "fish", "sea", "coral"));
            } else if (difficulty.equals("Medium")) {
                wordPools.put("Medium", Arrays.asList("dolphin", "whale", "stingray", "jellyfish"));
            } else {
                wordPools.put("Hard", Arrays.asList("oceanography", "shipwreck", "biodiversity"));
            }
        }


        wordPool = wordPools.get(difficulty);
        stageLabel.setText(island + " - " + difficulty);
    }


    private void startGame() {
        score = 0;
        scoreLabel.setText("Score: " + score);


        gameLoop = new Timeline(new KeyFrame(Duration.millis(2000), e -> spawnWord()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }


    private void spawnWord() {
        String word = wordPool.get(random.nextInt(wordPool.size()));
        Text wordText = new Text(word);
        wordText.setLayoutX(random.nextInt((int) gamePane.getWidth() - 100));
        wordText.setLayoutY(0);

        gamePane.getChildren().add(wordText);


        Timeline fall = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            wordText.setLayoutY(wordText.getLayoutY() + 5);
            if (wordText.getLayoutY() > gamePane.getHeight()) {
                gamePane.getChildren().remove(wordText);
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
    }


    private void checkInput() {
        String typedWord = inputField.getText().trim();

        for (javafx.scene.Node node : new ArrayList<>(gamePane.getChildren())) {
            if (node instanceof Text) {
                Text wordText = (Text) node;
                if (wordText.getText().equalsIgnoreCase(typedWord)) {
                    score++;
                    scoreLabel.setText("Score: " + score);
                    gamePane.getChildren().remove(wordText);
                    inputField.clear();
                    break;
                }
            }
        }
    }

    @FXML
    private void endGame() {
        if (currentUser != null) {
            if (score > currentUser.getHighScore()) {
                currentUser.setHighScore(score);
                userDAO.saveUser(currentUser);
            }
            System.out.println("Game Over! Final score: " + score);
            SceneManager.switchToLeaderboard(currentUser);
        } else {
            System.out.println("Error: No user logged in!");
        }
        SceneManager.switchToLeaderboard(currentUser);
    }
}
