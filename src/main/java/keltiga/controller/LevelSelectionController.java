package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import keltiga.model.User;

public class LevelSelectionController {
 
    @FXML private Button playButton;
    @FXML private Button switchUserButton;
    @FXML private Label userLabel;
    @FXML private Button javaIslandButton;
    @FXML private Button reefIslandButton;
    @FXML private Button deepIslandButton;
    @FXML private Button mapIslandButton;

    private String selectedIsland = "Java Island";
    private String selectedDifficulty = "Easy";
    private User currentUser;

    @FXML
    public void initialize() {
        this.currentUser = SceneManager.getCurrentUser();
        updateUserUI();
    }

    private void showDifficultyDialog(String island) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Difficulty");
        
        DialogPane dialogPane = dialog.getDialogPane();
        
        dialogPane.setStyle("-fx-background-color: #36AEC6; " +
                           "-fx-background-radius: 15px; " +
                           "-fx-border-radius: 15px; " +
                           "-fx-border-color: #2D91A6; " +
                           "-fx-border-width: 2px;");
        
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new javafx.geometry.Insets(25));
        buttonBox.setStyle("-fx-background-color: transparent;");
        
        Label titleLabel = new Label("SELECT DIFFICULTY");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        buttonBox.getChildren().add(titleLabel);
        
        String baseButtonStyle = "-fx-min-width: 220px; " +
                               "-fx-min-height: 45px; " +
                               "-fx-font-size: 16px; " +
                               "-fx-font-weight: bold; " +
                               "-fx-background-radius: 10px; " +
                               "-fx-cursor: hand; ";
        
        Button easyBtn = new Button("EASY");
        Button mediumBtn = new Button("MEDIUM");
        Button hardBtn = new Button("HARD");
        
        easyBtn.setStyle(baseButtonStyle + 
                        "-fx-background-color: #7ED957; " +
                        "-fx-text-fill: white;");
        easyBtn.setOnMouseEntered(e -> easyBtn.setStyle(baseButtonStyle + 
                        "-fx-background-color: #6BC348; -fx-text-fill: white;"));
        easyBtn.setOnMouseExited(e -> easyBtn.setStyle(baseButtonStyle + 
                        "-fx-background-color: #7ED957; -fx-text-fill: white;"));
        
        mediumBtn.setStyle(baseButtonStyle + 
                          "-fx-background-color: #FFB302; " +
                          "-fx-text-fill: white;");
        mediumBtn.setOnMouseEntered(e -> mediumBtn.setStyle(baseButtonStyle + 
                          "-fx-background-color: #E6A102; -fx-text-fill: white;"));
        mediumBtn.setOnMouseExited(e -> mediumBtn.setStyle(baseButtonStyle + 
                          "-fx-background-color: #FFB302; -fx-text-fill: white;"));
        
        hardBtn.setStyle(baseButtonStyle + 
                        "-fx-background-color: #FF4646; " +
                        "-fx-text-fill: white;");
        hardBtn.setOnMouseEntered(e -> hardBtn.setStyle(baseButtonStyle + 
                        "-fx-background-color: #E63E3E; -fx-text-fill: white;"));
        hardBtn.setOnMouseExited(e -> hardBtn.setStyle(baseButtonStyle + 
                        "-fx-background-color: #FF4646; -fx-text-fill: white;"));
        
        Label islandLabel = new Label(island);
        islandLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        buttonBox.getChildren().add(islandLabel);
        
        buttonBox.getChildren().addAll(easyBtn, mediumBtn, hardBtn);
        
        dialogPane.setContent(buttonBox);
        
        dialogPane.setPrefWidth(320);
        dialogPane.setPrefHeight(300);
        
        dialogPane.setHeaderText(null);
        
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);
        Node closeButton = dialogPane.lookupButton(ButtonType.CANCEL);
        closeButton.setVisible(false);
        
        easyBtn.setOnAction(e -> {
            selectedDifficulty = "Easy";
            selectedIsland = island;
            dialog.setResult("Easy");
            dialog.close();
            startGame();
        });
        
        mediumBtn.setOnAction(e -> {
            selectedDifficulty = "Medium";
            selectedIsland = island;
            dialog.setResult("Medium");
            dialog.close();
            startGame();
        });
        
        hardBtn.setOnAction(e -> {
            selectedDifficulty = "Hard";
            selectedIsland = island;
            dialog.setResult("Hard");
            dialog.close();
            startGame();
        });
        
        dialog.showAndWait();
    }

    @FXML
    private void selectJavaIsland() {
        showDifficultyDialog("Java Island");
    }

    @FXML
    private void selectReefIsland() {
        showDifficultyDialog("Reef Island");
    }

    @FXML
    private void selectDeepSeaIsland() {
        showDifficultyDialog("Deep Sea Island");
    }

    private void updateUserUI() {
        if (currentUser != null) {
            userLabel.setText("User: " + currentUser.getUsername());
        }
    }

    @FXML
    private void startGame() {
        SceneManager.startGame(selectedIsland, selectedDifficulty);
    }

    @FXML
    private void switchUser() {
        SceneManager.switchToUserSelection();
    }

    @FXML
    private void goToMap() {
        SceneManager.switchToMapIsland();
    }
}