package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import keltiga.dao.UserDAO;
import keltiga.model.User;

public class UserController {

    @FXML private TextField usernameField;
    @FXML private Label messageLabel;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    // Login existing user
    @FXML
    private void loginUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Username cannot be empty!");
            return;
        }

        currentUser = userDAO.getUser(username);
        if (currentUser == null) {
            messageLabel.setText("User not found. Try creating a new user.");
        } else {
            messageLabel.setText("Welcome back, " + username + "!");
            goToMainMenu();  // Proceed to the main menu
        }
    }

    // Create a new user
    @FXML
    private void createNewUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Username cannot be empty!");
            return;
        }

        if (userDAO.getUser(username) != null) {
            messageLabel.setText("User already exists. Please log in.");
            return;
        }

        currentUser = new User(username);
        userDAO.saveUser(currentUser);  // Save new user
        messageLabel.setText("New user created! Welcome, " + username + "!");
    }

    // Proceed to leaderboard page
    private void goToMainMenu() {
        // Example: switch to the level selection after login
        SceneManager.switchToLevelSelection();  // Or SceneManager.switchToLeaderboard(currentUser);
    }
}
