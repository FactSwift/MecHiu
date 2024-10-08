package org.keltiga.mechiu;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GameController {

    @FXML
    private Label scoreLabel;

    @FXML
    private TextField typingField;

    private int score = 0;

    @FXML
    protected void onWordTyped() {
        String typedWord = typingField.getText();
        if (isCorrectWord(typedWord)) {
            score += 10;
            scoreLabel.setText("Score: " + score);
        }
        typingField.clear(); // Clear the input after typing
    }

    @FXML
    protected void startGame() {
        // Start the game (initialize words, reset score, etc.)
        score = 0;
        scoreLabel.setText("Score: " + score);
    }

    private boolean isCorrectWord(String word) {
        // Replace this with your actual word-checking logic
        return true;  // Placeholder logic
    }
}
