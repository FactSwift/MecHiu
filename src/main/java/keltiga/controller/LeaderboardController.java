    package keltiga.controller;

    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.TableColumn;
    import javafx.scene.control.TableView;
    import javafx.scene.control.TextArea; // Add this import
    import javafx.scene.control.cell.PropertyValueFactory;
    import keltiga.dao.UserDAO;
    import keltiga.model.User;

    public class LeaderboardController {

        @FXML private TableView<User> leaderboardTable;
        @FXML private TableColumn<User, String> usernameColumn;
        @FXML private TableColumn<User, Integer> highScoreColumn;
        @FXML private TextArea currentPlayerRank; // Ensure the FXML annotation is present

        private UserDAO userDAO = new UserDAO();
        private User currentUser;

        @FXML
        public void initialize() {
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            highScoreColumn.setCellValueFactory(new PropertyValueFactory<>("highScore"));
            loadLeaderboard();
        }

        private void loadLeaderboard() {
            // Fetch all users from DAO
            Map<String, User> usersMap = userDAO.getAllUsers();

            // Filter out users with score 0
            List<User> validUsers = usersMap.values().stream()
                    .filter(user -> user.getHighScore() > 0)
                    .collect(Collectors.toList());

            // Sort users by high score in descending order
            List<User> sortedUsers = validUsers.stream()
                    .sorted((u1, u2) -> Integer.compare(u2.getHighScore(), u1.getHighScore()))
                    .collect(Collectors.toList());

            // Get the top 10 users
            List<User> topTenUsers = sortedUsers.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            // Find the rank of the current user based on table order
            int currentUserRank = -1;
            if (currentUser != null) {
                for (int i = 0; i < topTenUsers.size(); i++) {
                    if (topTenUsers.get(i).getUsername().equals(currentUser.getUsername())) {
                        currentUserRank = i + 1; // 1-based position
                        break;
                    }
                }
            }

            // Convert to ObservableList for TableView
            ObservableList<User> observableUserList = FXCollections.observableArrayList(topTenUsers);

            // Display top 10 in the table
            leaderboardTable.setItems(observableUserList);

            // Show current user's rank in TextArea
            if (currentPlayerRank != null) {
                if (currentUserRank > 0) {
                    // Clear the TextArea if in top 10
                    currentPlayerRank.clear();
                } else {
                    // Determine rank from full sorted list if not in top 10
                    currentUserRank = sortedUsers.indexOf(currentUser) + 1;
                    if (currentUserRank > 0) {
                        currentPlayerRank.setText("Anda berada di posisi #" + currentUserRank);
                    } else {
                        currentPlayerRank.setText("Anda tidak berada di dalam peringkat.");
                    }
                }
            }
        }




        @FXML
        private void goToLevelSelection() {
            SceneManager.switchToHome();
        }
    }
