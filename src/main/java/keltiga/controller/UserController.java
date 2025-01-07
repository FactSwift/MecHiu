package keltiga.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import keltiga.dao.UserDAO;
import keltiga.model.User;

public class UserController {

    @FXML private TextField usernameField;
    @FXML private Label messageLabel;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;
    private MediaPlayer backgroundMusic;

    @FXML
    public void initialize() {
        System.out.println("UserController initialized");
        playLoginMusic();
    }

    private void playLoginMusic() {
        try {
            String musicFile = "/music/login.mp3";
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            System.out.println("Attempting to play music from: " + musicFile);
            System.out.println("Full resource path: " + getClass().getResource(musicFile));
            
            Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.setVolume(1.0);
            backgroundMusic.play();
            
            System.out.println("Login music started playing");
        } catch (Exception e) {
            System.out.println("Error playing login music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
                backgroundMusic.dispose();
            } catch (Exception e) {
                System.out.println("Error stopping music: " + e.getMessage());
            }
        }
    }

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
            stopMusic();
            SceneManager.setCurrentUser(currentUser);
            SceneManager.switchToHome();
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
        userDAO.saveUser(currentUser);
        messageLabel.setText("New user created! Welcome, " + username + "!");
        stopMusic();
        SceneManager.setCurrentUser(currentUser);
        SceneManager.switchToHome();
    }

    @FXML
    private void deleteUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            messageLabel.setText("Username cannot be empty!");
            return;
        }

        User user = userDAO.getUser(username);
        if (user == null) {
            messageLabel.setText("User not found. Please try again.");
            return;
        }

        userDAO.deleteUser(username);
        messageLabel.setText("User " + username + " has been deleted.");
        usernameField.clear();
    }
}
