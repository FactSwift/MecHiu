package keltiga.model;

public class User {
    private String username;
    private int highScore;

    // Constructor
    public User(String username) {
        this.username = username;
        this.highScore = 0; // Default high score
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username (if needed)
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for highScore
    public int getHighScore() {
        return highScore;
    }

    // Setter for highScore
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}
