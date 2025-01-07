package keltiga.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private int highScore;
    private Map<String, String> unlockedIslands;

    // Constructor
    public User(String username) {
        this.username = username;
        this.highScore = 0; // Default high score
        this.unlockedIslands = new HashMap<>();
        // Sumatra selalu terbuka dengan difficulty easy
        this.unlockedIslands.put("Sumatra Island", "none");
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

    public Map<String, String> getUnlockedIslands() {
        return unlockedIslands;
    }

    public void setUnlockedIslands(Map<String, String> unlockedIslands) {
        this.unlockedIslands = unlockedIslands;
    }

    public boolean isIslandUnlocked(String island) {
        return unlockedIslands != null && unlockedIslands.containsKey(island);
    }

    public void updateIslandProgress(String island, String difficulty) {
        if (unlockedIslands == null) {
            unlockedIslands = new HashMap<>();
            unlockedIslands.put("Sumatra Island", "none");
        }
        
        // Update progress hanya jika lebih tinggi dari yang ada
        String currentProgress = unlockedIslands.getOrDefault(island, "none");
        if (shouldUpdateProgress(currentProgress, difficulty)) {
            unlockedIslands.put(island, difficulty);
        }
        
        // Unlock next island jika menyelesaikan hard
        if (difficulty.equals("hard")) {
            if (island.equals("Sumatra Island")) {
                unlockedIslands.putIfAbsent("Java Island", "none");
            } else if (island.equals("Java Island")) {
                unlockedIslands.putIfAbsent("Papua Island", "none");
            }
        }
    }

    private boolean shouldUpdateProgress(String currentProgress, String newProgress) {
        Map<String, Integer> progressLevel = new HashMap<>();
        progressLevel.put("none", 0);
        progressLevel.put("easy", 1);
        progressLevel.put("medium", 2);
        progressLevel.put("hard", 3);
        
        return progressLevel.get(newProgress.toLowerCase()) > 
               progressLevel.get(currentProgress.toLowerCase());
    }

    public boolean canPlayDifficulty(String island, String difficulty) {
        if (unlockedIslands == null) {
            unlockedIslands = new HashMap<>();
            unlockedIslands.put("Sumatra Island", "none");
        }

        if (!unlockedIslands.containsKey(island)) {
            return false;
        }

        String currentProgress = unlockedIslands.get(island);
        
        // Logic untuk unlock difficulty berdasarkan progress per pulau
        switch (currentProgress.toLowerCase()) {
            case "none":
                return difficulty.equals("easy");
            case "easy":
                return difficulty.equals("easy") || difficulty.equals("medium");
            case "medium":
                return !difficulty.equals("none");
            case "hard":
                return !difficulty.equals("none");
            default:
                return difficulty.equals("easy");
        }
    }
}
