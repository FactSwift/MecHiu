package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import keltiga.model.User;
import keltiga.controller.SceneManager;

public class LevelSelectionController {

    @FXML private Label userLabel;

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
    private void selectJavaIsland() {
        selectedIsland = "Java Island";
    }

    @FXML
    private void selectReefIsland() {
        selectedIsland = "Reef Island";
    }

    @FXML
    private void selectDeepSeaIsland() {
        selectedIsland = "Deep Sea Island";
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
}
