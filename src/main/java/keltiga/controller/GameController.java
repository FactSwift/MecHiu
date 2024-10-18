package keltiga.controller;

import keltiga.dao.UserDAO;
import keltiga.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class GameController {
    @FXML private TextField inputField;
    @FXML private TextField usernameField;
    @FXML private Label scoreLabel;
    @FXML private Label messageLabel;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;
    private int score = 0;

    public void initialize() {

        inputField.setOnKeyPressed(this::handleTyping);
    }


    public void loginUser() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            messageLabel.setText("Username cannot be empty!");
            return;
        }

        currentUser = userDAO.getUser(username);
        if (currentUser == null) {

            currentUser = new User(username);
            userDAO.saveUser(currentUser);
            messageLabel.setText("New user created: " + username);
        } else {

            messageLabel.setText("Welcome back, " + username + "!");
        }


        score = currentUser.getHighScore();
        scoreLabel.setText("Score: " + score);
    }


    private void handleTyping(KeyEvent event) {
        String typedWord = inputField.getText();

        if (typedWord.equalsIgnoreCase("test")) {
            score++;
            scoreLabel.setText("Score: " + score);
            inputField.clear();
        }
    }


    public void endGame() {
        if (currentUser != null) {

            if (score > currentUser.getHighScore()) {
                currentUser.setHighScore(score);
                userDAO.saveUser(currentUser);
            }
            messageLabel.setText("Game Over! Final score: " + score);
        }
    }
}
