package keltiga.model;

public class User {
    private String username;
    private int highScore;

    // Constructor
    public User(String username) {
        this.username = username;
        this.highScore = 0; // Default high score
    }

    // Getter
    public String getUsername() {
        return username;
    }

    // Setter
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter
    public int getHighScore() {
        return highScore;
    }

    // Setter
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}
