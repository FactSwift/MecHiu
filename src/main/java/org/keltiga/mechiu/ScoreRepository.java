package org.keltiga.mechiu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ScoreRepository {

    public void saveScore(int userId, int score) throws SQLException {
        String sql = "INSERT INTO scores (user_id, score) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        }
    }
}