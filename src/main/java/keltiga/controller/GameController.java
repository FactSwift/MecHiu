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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
    @FXML private Button javaIslandButton; 
    @FXML private Button reefIslandButton; 
    @FXML private Button deepIslandButton; 
    @FXML private ImageView backgroundImage;
    
    private int score = 0;
    private int health = 3;

    private Timeline gameLoop;
    private Random random = new Random();
    private List<String> wordPool;
    private User currentUser;
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    // Map to store words and their spawn times
    private Map<TextFlow, Long> wordSpawnTimes = new HashMap<TextFlow, Long>();

    private List<String> obstaclePool;
    private int wordsCompleted = 0;
    private int wordsTarget;
    
    // Tambahkan variabel untuk menyimpan pulau dan difficulty yang dipilih
    private String selectedIsland;
    private String selectedDifficulty;

    private MediaPlayer backgroundMusic;

    @FXML
    public void initialize() {
        this.currentUser = SceneManager.getCurrentUser();
        inputField.setOnAction(event -> checkInput());
    }
    
    private void updateUI() {
        if (healthLabel != null) {
            healthLabel.setText("Health: " + health);
        }
        scoreLabel.setText("Score: " + score);
        healthLabel.setText("Health: " + health);
    }

    public void startGame(String island, String difficulty) {
        stopCurrentMusic();
        playIslandMusic(island);
        
        this.selectedIsland = island;
        this.selectedDifficulty = difficulty;
        
        // Set background based on selected island
        String backgroundPath;
        switch (island) {
            case "Sumatra Island":
                backgroundPath = "/view/Image/BackgroundSumatra.png";
                break;
            case "Java Island":
                backgroundPath = "/view/Image/BackgroundJava.png";
                break;
            case "Papua Island":
                backgroundPath = "/view/Image/BackgroundPapua.png";
                break;
            default:
                backgroundPath = "/view/Image/BackgroundSumatra.png";
                break;
        }
        
        backgroundImage.setImage(new Image(getClass().getResourceAsStream(backgroundPath)));
        
        // Set target berdasarkan difficulty
        switch (difficulty.toLowerCase()) {
            case "easy":
                wordsTarget = 15;
                break;
            case "medium":
                wordsTarget = 25;
                break;
            case "hard":
                wordsTarget = 35;
                break;
        }
        
        loadWordPool(island, difficulty);
        loadObstaclePool(difficulty);
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

    private void loadObstaclePool(String difficulty) {
        try (FileReader reader = new FileReader("data/Obstacles.json")) {
            Map<String, List<String>> obstaclePools = gson.fromJson(reader, Map.class);
            if (obstaclePools != null && obstaclePools.containsKey(difficulty.toLowerCase())) {
                obstaclePool = obstaclePools.get(difficulty.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void spawnWord() {
        if (wordPool == null || wordPool.isEmpty()) {
            return;
        }

        String word;
        boolean isObstacle = random.nextDouble() < 0.15;
        if (isObstacle && obstaclePool != null && !obstaclePool.isEmpty()) {
            word = obstaclePool.get(random.nextInt(obstaclePool.size()));
            Text wordText = new Text(word);
            wordText.setStyle("-fx-font-size: 16px; -fx-fill: #FF4040; -fx-font-weight: bold; -fx-opacity: 1.0;");
            
            TextFlow wordWrapper = new TextFlow(wordText);
            wordWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
            wordWrapper.setPrefHeight(Region.USE_COMPUTED_SIZE);
            wordWrapper.setMaxWidth(Region.USE_PREF_SIZE);
            wordWrapper.setMaxHeight(Region.USE_PREF_SIZE);
            wordWrapper.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 5 10 5 10; -fx-background-radius: 5;");
            
            ImageView obstacleImage = new ImageView(new Image(getClass().getResourceAsStream("/view/Image/obstacle.png")));
            obstacleImage.setFitWidth(180);
            obstacleImage.setFitHeight(90);
            
            StackPane container = new StackPane(obstacleImage, wordWrapper);
            container.setOpacity(1.0);
            
            StackPane.setAlignment(wordWrapper, Pos.CENTER);
            container.setAlignment(Pos.CENTER);
            
            StackPane.setMargin(wordWrapper, new Insets(15, 10, 0, 0));
            
            container.setLayoutX(gamePane.getWidth());
            container.setLayoutY(random.nextInt((int) (gamePane.getHeight() - 100)));

            gamePane.getChildren().add(container);
            wordSpawnTimes.put(wordWrapper, System.currentTimeMillis());

            Timeline movement = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                container.setLayoutX(container.getLayoutX() - 5);
                if (container.getLayoutX() < -100) {
                    if (gamePane.getChildren().contains(container)) {
                        gamePane.getChildren().remove(container);
                        wordSpawnTimes.remove(wordWrapper);
                    }
                }
            }));
            movement.setCycleCount(Timeline.INDEFINITE);
            movement.play();

            wordWrapper.setUserData(Boolean.TRUE);
        } else {
            word = wordPool.get(random.nextInt(wordPool.size()));
            Text wordText = new Text(word);
            wordText.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-font-weight: bold;");
            
            TextFlow wordWrapper = new TextFlow(wordText);
            wordWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
            wordWrapper.setPrefHeight(Region.USE_COMPUTED_SIZE);
            wordWrapper.setMaxWidth(Region.USE_PREF_SIZE);
            wordWrapper.setMaxHeight(Region.USE_PREF_SIZE);
            wordWrapper.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 5 10 5 10; -fx-background-radius: 5;");
            
            String fishImagePath = getFishSpritePath(selectedIsland, selectedDifficulty);
            ImageView fishImage = new ImageView(new Image(getClass().getResourceAsStream(fishImagePath)));
            fishImage.setFitWidth(180);
            fishImage.setFitHeight(90);
            
            StackPane container = new StackPane();
            container.getChildren().addAll(fishImage, wordWrapper);
            container.setOpacity(1.0);
            
            StackPane.setAlignment(wordWrapper, Pos.TOP_CENTER);
            StackPane.setMargin(wordWrapper, new Insets(0, 0, 100, 0));
            container.setAlignment(Pos.CENTER);
            
            container.setLayoutX(gamePane.getWidth());
            container.setLayoutY(random.nextInt((int) (gamePane.getHeight() - 100)));

            gamePane.getChildren().add(container);
            wordSpawnTimes.put(wordWrapper, System.currentTimeMillis());

            Timeline movement = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                container.setLayoutX(container.getLayoutX() - 5);
                if (container.getLayoutX() < -100) {
                    if (gamePane.getChildren().contains(container)) {
                        gamePane.getChildren().remove(container);
                        wordSpawnTimes.remove(wordWrapper);
                        wordMissed();
                    }
                }
            }));
            movement.setCycleCount(Timeline.INDEFINITE);
            movement.play();

            wordWrapper.setUserData(Boolean.FALSE);
        }
    }

    private String getFishSpritePath(String island, String difficulty) {
        String basePath = "/view/Image/";
        
        if (island.equals("Sumatra Island")) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return basePath + "Ikan1.png";
                case "medium":
                    return basePath + "Ikan2.png";
                case "hard":
                    return basePath + "Ikan3.png";
            }
        } else if (island.equals("Java Island")) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return basePath + "Ikan4.png";
                case "medium":
                    return basePath + "Ikan5.png";
                case "hard":
                    return basePath + "Ikan6.png";
            }
        } else if (island.equals("Papua Island")) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return basePath + "Ikan7.png";
                case "medium":
                    return basePath + "Ikan8.png";
                case "hard":
                    return basePath + "Ikan9.png";
            }
        }
        
        // Default fallback jika ada kesalahan
        return basePath + "Ikan1.png";
    }

    private void wordMissed() {
        health--;
        updateUI();
        if (health <= 0) {
            loseGame();
        }
    }

    private void checkInput() {
        String typedWord = inputField.getText().trim();
        inputField.clear();

        for (javafx.scene.Node node : gamePane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane container = (StackPane) node;
                for (javafx.scene.Node child : container.getChildren()) {
                    if (child instanceof TextFlow) {
                        TextFlow textFlow = (TextFlow) child;
                        Text wordText = (Text) textFlow.getChildren().get(0);

                        if (wordText.getText().equalsIgnoreCase(typedWord)) {
                            double currentX = container.getLayoutX();
                            
                            gamePane.getChildren().remove(container);
                            wordSpawnTimes.remove(textFlow);
                            
                            boolean isObstacle = textFlow.getUserData() != null ? 
                                (Boolean) textFlow.getUserData() : false;
                            
                            if (isObstacle) {
                                score = Math.max(0, score - 55);
                                health--;
                                updateUI();
                                if (health <= 0) {
                                    loseGame();
                                }
                            } else {
                                wordsCompleted++;
                                calculateScore(currentX);
                                if (wordsCompleted >= wordsTarget) {
                                    winGame();
                                }
                            }
                            updateUI();
                            return;
                        }
                    }
                }
            }
        }
    }


    private void calculateScore(double currentX) {
        double maxScore = 125;
        double initialPosition = gamePane.getWidth();
        double edgePosition = -100;
        double totalDistance = initialPosition - edgePosition;
        
        double distanceFromSpawn = initialPosition - currentX;
        double percentageTravel = distanceFromSpawn / totalDistance;
        
        int earnedScore = (int)(maxScore * (1 - percentageTravel));
        score += Math.max(1, earnedScore);
        System.out.println("Word typed with position score: " + earnedScore);
    }

    @FXML
    public void endGame() {
        stopCurrentMusic();
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        // Simpan score sebelum pindah ke leaderboard
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);
        }
        
        // Langsung ke Home screen ketika end game
        SceneManager.switchToHome();
    }

    private void loseGame() {
        stopCurrentMusic();
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        // Simpan score sebelum pindah ke leaderboard
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);
        }
        
        // Langsung ke Home screen ketika kalah
        SceneManager.switchToHome();
    }

    private void winGame() {
        stopCurrentMusic();
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        // Pindah ke infographic untuk kasus menang
        SceneManager.switchToInfographic(selectedIsland, selectedDifficulty, score);
    }

    private void stopCurrentMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
    }

    private void playIslandMusic(String island) {
        String musicFile;
        switch (island) {
            case "Java Island":
                musicFile = "/music/jawa.mp3";
                break;
            case "Sumatra Island":
                musicFile = "/music/sumatra.mp3";
                break;
            case "Papua Island":
                musicFile = "/music/papua.mp3";
                break;
            default:
                return;
        }
        
        try {
            Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.play();
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }
}
