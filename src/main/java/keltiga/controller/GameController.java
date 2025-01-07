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
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.animation.Animation;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.File;

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

    // Tambahkan method untuk mengecek apakah game sudah berakhir
    private boolean isGameOver = false;

    private boolean isGameActive = false;
    private List<Timeline> activeTimelines = new ArrayList<>();

    private static boolean isQuitting = false;
    private static boolean isProcessingQuit = false;

    private enum GameEndState {
        NONE, QUIT, GAME_OVER
    }
    
    private GameEndState endState = GameEndState.NONE;

    private class FishSprite {
        private final Image spriteSheet;
        private ImageView spriteView;
        private final int frameWidth = 666;
        private final int frameHeight = 375;
        private int fishStartY;
        private final int FRAME_COUNT = 12;
        private final int COLUMNS = 6;
        private final int ROWS = 2;
        private final int ANIMATION_DURATION = 100;
        
        public FishSprite(String type) {
            try {
                String spritePath = "/view/Image/fishes_sprite.png";
                var resourceUrl = getClass().getResource(spritePath);
                if (resourceUrl == null) {
                    System.out.println("Error: Cannot find sprite sheet at " + spritePath);
                    throw new RuntimeException("Sprite sheet not found");
                }
                
                this.spriteSheet = new Image(resourceUrl.toExternalForm(), true);
                
                switch(type.toLowerCase()) {
                    case "sumatra_easy": fishStartY = 0; break;
                    case "sumatra_medium": fishStartY = frameHeight * 2; break;
                    case "sumatra_hard": fishStartY = frameHeight * 4; break;
                    case "java_easy": fishStartY = frameHeight * 6; break;
                    case "java_medium": fishStartY = frameHeight * 8; break;
                    case "java_hard": fishStartY = frameHeight * 10; break;
                    case "papua_easy": fishStartY = frameHeight * 12; break;
                    case "papua_medium": fishStartY = frameHeight * 14; break;
                    case "papua_hard": fishStartY = frameHeight * 16; break;
                    default: fishStartY = 0; break;
                }
            } catch (Exception e) {
                System.out.println("Error initializing sprite: " + e.getMessage());
                throw new RuntimeException("Failed to initialize sprite", e);
            }
        }
        
        public ImageView createAnimatedSprite() {
            ImageView spriteView = new ImageView(spriteSheet);
            spriteView.setFitWidth(222);
            spriteView.setFitHeight(117);
            spriteView.setPreserveRatio(true);
            
            Timeline animation = new Timeline();
            
            for (int i = 0; i < FRAME_COUNT; i++) {
                final int currentRow = i / COLUMNS;
                final int currentCol = i % COLUMNS;
                
                final int frameX = currentCol * frameWidth;
                final int frameY = fishStartY + (currentRow * frameHeight);
                
                KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * ANIMATION_DURATION),
                    e -> spriteView.setViewport(new Rectangle2D(
                        frameX, frameY, frameWidth, frameHeight
                    ))
                );
                animation.getKeyFrames().add(keyFrame);
            }
            
            animation.setCycleCount(Timeline.INDEFINITE);
            activeTimelines.add(animation);
            animation.play();
            
            return spriteView;
        }

        public void dispose() {
            if (spriteView != null) {
                spriteView.setImage(null);
                spriteView = null;
            }
        }
    }

    private String getFishSpriteType(String island, String difficulty) {
        return island.toLowerCase().split(" ")[0] + "_" + difficulty.toLowerCase();
    }

    private void updateFishAnimation() {
        for (Node node : gamePane.getChildren()) {
            if (node instanceof ImageView && node.getId() != null) {
                ImageView fishView = (ImageView) node;
                String fishType = fishView.getId();
                
                FishSprite sprite = fishSprites.computeIfAbsent(
                    fishType, 
                    k -> new FishSprite(fishType)
                );
                
                ImageView newFrame = sprite.createAnimatedSprite();
                fishView.setImage(newFrame.getImage());
                fishView.setViewport(newFrame.getViewport());
            }
        }
    }

    private Map<String, FishSprite> fishSprites = new HashMap<>();

    // Tambahkan variabel untuk sound effects
    private MediaPlayer typingSoundPlayer;
    private MediaPlayer badSoundPlayer;
    private MediaPlayer fishDiesSoundPlayer;
    private List<Media> typingSounds;
    private Media badSound;
    private Media fishDiesSound;
    private Random soundRandom = new Random();

    @FXML
    public void initialize() {
        this.currentUser = SceneManager.getCurrentUser();
        inputField.setOnAction(event -> checkInput());
        
        // Tambahkan event listener untuk keystroke
        inputField.setOnKeyPressed(event -> {
            if (isGameActive && !isGameOver) {
                playTypingSound();
            }
        });
        
        // Inisialisasi sound effects
        initializeSoundEffects();
        
        // Update animasi setiap 100ms
        Timeline animation = new Timeline(
            new KeyFrame(Duration.millis(100), e -> updateFishAnimation())
        );
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    private void initializeSoundEffects() {
        try {
            // Inisialisasi typing sounds
            typingSounds = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                String soundPath = "/sfx/type" + i + ".mp3";
                var resource = getClass().getResource(soundPath);
                if (resource != null) {
                    typingSounds.add(new Media(resource.toExternalForm()));
                    System.out.println("Successfully loaded: " + soundPath);
                } else {
                    System.out.println("Tidak dapat menemukan file suara: " + soundPath);
                }
            }
            
            System.out.println("Total typing sounds loaded: " + typingSounds.size());

            // Inisialisasi obstacle dan fish die sounds
            var badResource = getClass().getResource("/sfx/bad.mp3");
            var fishDiesResource = getClass().getResource("/sfx/piranhadies1.mp3");
            
            if (badResource != null) {
                badSound = new Media(badResource.toExternalForm());
            } else {
                System.out.println("Tidak dapat menemukan file suara: /sfx/bad.mp3");
            }
            
            if (fishDiesResource != null) {
                fishDiesSound = new Media(fishDiesResource.toExternalForm());
            } else {
                System.out.println("Tidak dapat menemukan file suara: /sfx/piranhadies1.mp3");
            }
        } catch (Exception e) {
            System.out.println("Error initializing sound effects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playTypingSound() {
        try {
            if (!typingSounds.isEmpty()) {
                Media randomSound = typingSounds.get(soundRandom.nextInt(typingSounds.size()));
                // Buat MediaPlayer baru untuk setiap ketikan
                MediaPlayer newSoundPlayer = new MediaPlayer(randomSound);
                newSoundPlayer.setVolume(1.0);
                
                // Set handler untuk membersihkan resources setelah selesai
                newSoundPlayer.setOnEndOfMedia(() -> {
                    newSoundPlayer.dispose();
                });
                
                newSoundPlayer.play();
                
                // Debug log
                System.out.println("Playing typing sound...");
            } else {
                System.out.println("Warning: typingSounds list is empty!");
            }
        } catch (Exception e) {
            System.out.println("Error playing typing sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playBadSound() {
        try {
            if (badSound != null) {
                badSoundPlayer = new MediaPlayer(badSound);
                badSoundPlayer.play();
            }
        } catch (Exception e) {
            System.out.println("Error playing bad sound: " + e.getMessage());
        }
    }

    private void playFishDiesSound() {
        try {
            if (fishDiesSound != null) {
                fishDiesSoundPlayer = new MediaPlayer(fishDiesSound);
                fishDiesSoundPlayer.play();
            }
        } catch (Exception e) {
            System.out.println("Error playing fish dies sound: " + e.getMessage());
        }
    }

    private void updateUI() {
        if (!isGameActive || isGameOver || endState == GameEndState.QUIT) {
            return;
        }
        if (healthLabel != null) {
            healthLabel.setText("Health: " + health);
        }
        scoreLabel.setText("Score: " + score);
        healthLabel.setText("Health: " + health);
    }

    public void startGame(String island, String difficulty) {
        try {
            // Reset state
            cleanup();
            
            // Set game state
            this.selectedIsland = island;
            this.selectedDifficulty = difficulty;
            isGameActive = true;
            isGameOver = false;
            health = 3;
            score = 0;
            wordsCompleted = 0;
            
            // Set background
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
            }
            
            Platform.runLater(() -> {
                try {
                    backgroundImage.setImage(new Image(getClass().getResourceAsStream(backgroundPath)));
                    
                    // Set target berdasarkan difficulty
                    switch (difficulty.toLowerCase()) {
                        case "easy": wordsTarget = 15; break;
                        case "medium": wordsTarget = 25; break;
                        case "hard": wordsTarget = 35; break;
                    }
                    
                    // Load resources
                    loadWordPool(island, difficulty);
                    loadObstaclePool(difficulty);
                    updateUI();
                    
                    // Start game loop
                    gameLoop = new Timeline(new KeyFrame(Duration.millis(2000), e -> spawnWord()));
                    gameLoop.setCycleCount(Timeline.INDEFINITE);
                    activeTimelines.add(gameLoop);
                    gameLoop.play();
                    
                    // Start music
                    playIslandMusic(island);
                    
                } catch (Exception e) {
                    System.out.println("Error in startGame UI initialization: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.out.println("Error in startGame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadWordPool(String island, String difficulty) {
        String installDir = System.getProperty("user.dir");
        String jsonPath = installDir + "/data/" + island + ".json";
        File jsonFile = new File(jsonPath);
        
        System.out.println("=== Debug Info ===");
        System.out.println("Install Dir: " + installDir);
        System.out.println("JSON Path: " + jsonPath);
        System.out.println("File exists: " + jsonFile.exists());
        System.out.println("File can read: " + jsonFile.canRead());
        System.out.println("File absolute path: " + jsonFile.getAbsolutePath());
        System.out.println("=================");
        
        try (FileReader reader = new FileReader(jsonFile)) {
            Gson gson = new Gson();
            Map<String, List<String>> wordPools = gson.fromJson(reader, Map.class);

            System.out.println("Reading from: " + jsonPath);
            System.out.println("Loaded word pool from JSON: " + wordPools);

            if (wordPools != null && wordPools.containsKey(difficulty.toLowerCase())) {
                wordPool = wordPools.get(difficulty.toLowerCase());
                System.out.println("Words for " + difficulty + " difficulty: " + wordPool);
            } else {
                System.out.println("No words available for the selected difficulty: " + difficulty);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading word pool from: " + jsonPath);
            System.out.println("Current directory: " + installDir);
            
            // List files in data directory
            File dataDir = new File(installDir + "/data");
            System.out.println("Files in data directory:");
            if (dataDir.exists() && dataDir.isDirectory()) {
                for (File file : dataDir.listFiles()) {
                    System.out.println(" - " + file.getName());
                }
            } else {
                System.out.println("Data directory does not exist or is not a directory");
            }
        }
    }

    private void loadObstaclePool(String difficulty) {
        String installDir = System.getProperty("user.dir");
        String jsonPath = installDir + "/data/Obstacles.json";
        
        try (FileReader reader = new FileReader(jsonPath)) {
            Map<String, List<String>> obstaclePools = gson.fromJson(reader, 
                new TypeToken<Map<String, List<String>>>(){}.getType());
                
            System.out.println("Reading obstacles from: " + jsonPath);
            
            if (obstaclePools != null && obstaclePools.containsKey(difficulty.toLowerCase())) {
                obstaclePool = obstaclePools.get(difficulty.toLowerCase());
                System.out.println("Loaded obstacles: " + obstaclePool);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading obstacles from: " + jsonPath);
            System.out.println("Current directory: " + installDir);
        }
    }

    private void spawnWord() {
        if (!isGameActive || isGameOver || endState == GameEndState.QUIT) {
            return;
        }

        try {
            String word;
            boolean isObstacle = random.nextDouble() < 0.30;
            if (isObstacle && obstaclePool != null && !obstaclePool.isEmpty()) {
                word = obstaclePool.get(random.nextInt(obstaclePool.size()));
                Text wordText = new Text(word);
                wordText.setStyle("-fx-font-size: 16px; -fx-fill: white; -fx-font-weight: bold;");
                
                TextFlow wordWrapper = new TextFlow(wordText);
                wordWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
                wordWrapper.setPrefHeight(Region.USE_COMPUTED_SIZE);
                wordWrapper.setMaxWidth(Region.USE_PREF_SIZE);
                wordWrapper.setMaxHeight(Region.USE_PREF_SIZE);
                wordWrapper.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 5 10 5 10; -fx-background-radius: 5;");
                
                StackPane container = new StackPane();
                ImageView obstacleImage = new ImageView(new Image(getClass().getResourceAsStream("/view/Image/obstacle.png")));
                obstacleImage.setFitWidth(180);
                obstacleImage.setFitHeight(90);
                
                container.getChildren().addAll(obstacleImage, wordWrapper);
                StackPane.setAlignment(wordWrapper, Pos.TOP_CENTER);
                StackPane.setMargin(wordWrapper, new Insets(0, 0, 100, 0));
                container.setLayoutX(gamePane.getWidth());
                container.setLayoutY(random.nextInt((int) (gamePane.getHeight() - 100)));

                gamePane.getChildren().add(container);
                wordSpawnTimes.put(wordWrapper, System.currentTimeMillis());

                final Timeline movement = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                    if (!isGameActive || !gamePane.isVisible()) {
                        event.consume();
                        return;
                    }
                    
                    container.setLayoutX(container.getLayoutX() - 5);
                    if (container.getLayoutX() < -100) {
                        Platform.runLater(() -> {
                            if (gamePane.getChildren().contains(container)) {
                                gamePane.getChildren().remove(container);
                                wordSpawnTimes.remove(wordWrapper);
                                // Obstacle tidak mengurangi health saat keluar layar
                            }
                        });
                    }
                }));
                movement.setCycleCount(Timeline.INDEFINITE);
                activeTimelines.add(movement);
                movement.play();

                wordWrapper.setUserData(Boolean.TRUE); // Mark as obstacle
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
                
                String fishType = getFishSpriteType(selectedIsland, selectedDifficulty);
                FishSprite sprite = fishSprites.computeIfAbsent(fishType, k -> new FishSprite(fishType));
                ImageView fishImage = sprite.createAnimatedSprite();
                fishImage.setId(fishType);
                
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

                final Timeline movement = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                    if (!isGameActive) {
                        event.consume();
                        return;
                    }
                    
                    container.setLayoutX(container.getLayoutX() - 5);
                    if (container.getLayoutX() < -100) {
                        Platform.runLater(() -> {
                            if (gamePane.getChildren().contains(container)) {
                                gamePane.getChildren().remove(container);
                                wordSpawnTimes.remove(wordWrapper);
                                if (isGameActive && !isGameOver) {
                                    wordMissed();
                                }
                            }
                        });
                    }
                }));
                movement.setCycleCount(Timeline.INDEFINITE);
                activeTimelines.add(movement);
                movement.play();

                wordWrapper.setUserData(Boolean.FALSE);
            }
        } catch (Exception e) {
            System.out.println("Error in spawnWord: " + e.getMessage());
        }
    }

    private void wordMissed() {
        if (!isGameActive || isGameOver) return;
        
        health--;
        updateUI();
        
        if (health <= 0) {
            gameOver();
        }
    }

    private void checkInput() {
        if (!isGameActive || isGameOver || endState == GameEndState.QUIT) {
            return;
        }
        
        // Play typing sound BEFORE processing input
        Platform.runLater(() -> playTypingSound());
        
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
                                playBadSound(); // Play bad sound for obstacles
                                updateUI();
                                if (health <= 0) {
                                    loseGame();
                                }
                            } else {
                                wordsCompleted++;
                                calculateScore(currentX);
                                playFishDiesSound(); // Play fish dies sound for correct words
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
        isGameActive = false;
        isGameOver = true;
        
        // Hentikan game loop dan timeline
        if (gameLoop != null) {
            gameLoop.stop();
        }
        for (Timeline timeline : activeTimelines) {
            if (timeline != null) {
                timeline.stop();
            }
        }
        activeTimelines.clear();
        
        // Hentikan musik
        stopCurrentMusic();
        
        // Bersihkan resources
        for (FishSprite sprite : fishSprites.values()) {
            sprite.dispose();
        }
        fishSprites.clear();
        
        if (gamePane != null) {
            gamePane.getChildren().clear();
        }
        
        // Simpan score jika lebih tinggi
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);
        }
        
        // Langsung ke game over screen
        Platform.runLater(() -> {
            SceneManager.switchToGameOver(selectedIsland, selectedDifficulty, score);
        });
    }

    private void loseGame() {
        if (isGameOver || endState != GameEndState.NONE) return;
        
        isGameOver = true;
        isGameActive = false;
        endState = GameEndState.GAME_OVER;
        
        // Save score first
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);
        }
        
        final int finalScore = score;
        final String finalIsland = selectedIsland;
        final String finalDifficulty = selectedDifficulty;
        
        cleanup();
        
        Platform.runLater(() -> {
            SceneManager.switchToGameOver(finalIsland, finalDifficulty, finalScore);
        });
    }

    private void winGame() {
        try {
            cleanup();
            final String finalIsland = selectedIsland;
            final String finalDifficulty = selectedDifficulty;
            final int finalScore = score;

            // Update progress dan simpan score
            if (currentUser != null) {
                currentUser.updateIslandProgress(selectedIsland, selectedDifficulty.toLowerCase());
                if (score > currentUser.getHighScore()) {
                    currentUser.setHighScore(score);
                }
                userDAO.saveUser(currentUser);
            }

            Platform.runLater(() -> {
                try {
                    Thread.sleep(500); // Beri waktu untuk cleanup
                    SceneManager.switchToInfographic(finalIsland, finalDifficulty, finalScore);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.out.println("Error in winGame: " + e.getMessage());
            e.printStackTrace();
            SceneManager.switchToHome(); // Fallback ke home jika error
        }
    }

    private void stopCurrentMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
                backgroundMusic.dispose();
                backgroundMusic = null;
            } catch (Exception e) {
                System.out.println("Error stopping music: " + e.getMessage());
            }
        }
    }

    private void playIslandMusic(String island) {
        try {
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
            
            var musicResource = getClass().getResource(musicFile);
            if (musicResource != null) {
                Media sound = new Media(musicResource.toExternalForm());
                backgroundMusic = new MediaPlayer(sound);
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.play();
            } else {
                System.out.println("Music file not found: " + musicFile);
            }
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkGameOver() {
        if (isQuitting || isProcessingQuit) return;
        
        if (health <= 0) {
            gameOver();
        } else if (wordsCompleted >= wordsTarget) {
            winGame();
        }
    }

    private void gameOver() {
        if (!isGameActive || isGameOver) return;
        
        // Set flags
        isGameActive = false;
        isGameOver = true;
        endState = GameEndState.GAME_OVER;
        
        // Hentikan game loop
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        
        // Hentikan semua timeline
        for (Timeline timeline : activeTimelines) {
            if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
            }
        }
        activeTimelines.clear();
        
        // Hentikan musik
        stopCurrentMusic();
        
        // Bersihkan sprite dan gamePane
        for (FishSprite sprite : fishSprites.values()) {
            sprite.dispose();
        }
        fishSprites.clear();
        
        if (gamePane != null) {
            Platform.runLater(() -> gamePane.getChildren().clear());
        }
        
        // Simpan score jika lebih tinggi
        if (currentUser != null && score > currentUser.getHighScore()) {
            currentUser.setHighScore(score);
            userDAO.saveUser(currentUser);
        }
        
        final int finalScore = score;
        final String finalIsland = selectedIsland;
        final String finalDifficulty = selectedDifficulty;
        
        // Pindah ke game over screen
        Platform.runLater(() -> {
            SceneManager.switchToGameOver(finalIsland, finalDifficulty, finalScore);
        });
    }

    private void cleanup() {
        if (isGameOver || !isGameActive) return;
        
        isGameActive = false;
        stopAllTimelines();
        clearGameState();
        
        // Cleanup sound players
        if (typingSoundPlayer != null) {
            typingSoundPlayer.dispose();
        }
        if (badSoundPlayer != null) {
            badSoundPlayer.dispose();
        }
        if (fishDiesSoundPlayer != null) {
            fishDiesSoundPlayer.dispose();
        }
    }

    private void stopAllTimelines() {
        if (gameLoop != null) {
            try {
                gameLoop.stop();
                gameLoop = null;
            } catch (Exception e) {
                System.out.println("Error stopping game loop: " + e.getMessage());
            }
        }
        
        for (Timeline timeline : new ArrayList<>(activeTimelines)) {
            try {
                if (timeline != null) {
                    timeline.stop();
                }
            } catch (Exception e) {
                System.out.println("Error stopping timeline: " + e.getMessage());
            }
        }
        activeTimelines.clear();
    }

    private void clearGameState() {
        try {
            if (gamePane != null) {
                gamePane.getChildren().clear();
            }
            if (wordSpawnTimes != null) {
                wordSpawnTimes.clear();
            }
            fishSprites.clear();
            stopCurrentMusic();
        } catch (Exception e) {
            System.out.println("Error clearing game state: " + e.getMessage());
        }
    }

    @FXML
    private void backToMap() {
        isProcessingQuit = true;
        isQuitting = true;
        isGameActive = false;
        endState = GameEndState.QUIT;
        cleanup();
        Platform.runLater(() -> {
            SceneManager.switchToHome();
            resetQuitState();
        });
    }

    private static void resetQuitState() {
        isQuitting = false;
        isProcessingQuit = false;
    }

    public static boolean isQuitting() {
        return isQuitting;
    }

    public static boolean isProcessingQuit() {
        return isProcessingQuit;
    }
}
