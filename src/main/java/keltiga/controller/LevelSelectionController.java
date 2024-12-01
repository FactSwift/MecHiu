package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserUI();
    }


    private void updateUserUI() {
        if (currentUser != null) {
            userLabel.setText("User: " + currentUser.getUsername());
        }
    }

    @FXML
    private void selectMapIsland() {
        SceneManager.switchToMapIsland();
    }

    @FXML
    private void selectJavaIsland() {
        selectedIsland = "Java Island";
        // Tambahkan logika toggle
        toggleButtonState(javaIslandButton);
    }

    @FXML
    private void selectReefIsland() {
        selectedIsland = "Reef Island";
        // Tambahkan logika toggle
        toggleButtonState(reefIslandButton);
    }

    @FXML
    private void selectDeepSeaIsland() {
        selectedIsland = "Deep Sea Island";
        // Tambahkan logika toggle
        toggleButtonState(deepIslandButton);
    }


    @FXML
    private void selectDifficultyEasy() {
        selectedDifficulty = "Easy";
    }

    @FXML
    private void selectDifficultyMedium() {
        selectedDifficulty = "Medium";
    }

    @FXML
    private void selectDifficultyHard() {
        selectedDifficulty = "Hard";
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

    

    private void toggleButtonState(Button button) {
        // Toggle state by toggling the CSS class
        if (button.getStyleClass().contains("toggled")) {
            button.getStyleClass().remove("toggled");
        } else {
            button.getStyleClass().add("toggled");
        }
    }

}