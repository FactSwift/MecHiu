package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import keltiga.dao.UserDAO;
import keltiga.model.User;
import keltiga.controller.SceneManager;

public class UserController {

    @FXML private TextField usernameField;
    @FXML private Label messageLabel;

    private UserDAO userDAO = new UserDAO();  // DAO to manage user data
    private User currentUser;


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
            SceneManager.setCurrentUser(currentUser);  // Set current user in SceneManager
            SceneManager.switchToLevelSelection();  // Proceed to level selection
        }
    }


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
        SceneManager.setCurrentUser(currentUser);  // Set current user
        SceneManager.switchToLevelSelection();  // Proceed to level selection
    }
}
