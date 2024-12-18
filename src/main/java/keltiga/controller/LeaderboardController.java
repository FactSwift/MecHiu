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
            this.currentUser = SceneManager.getCurrentUser();
            
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            highScoreColumn.setCellValueFactory(new PropertyValueFactory<>("highScore"));
            loadLeaderboard();
        }

        private void loadLeaderboard() {
            // Fetch all users from DAO
            Map<String, User> usersMap = userDAO.getAllUsers();

            // Filter out users with score 0 and sort by high score
            List<User> sortedUsers = usersMap.values().stream()
                    .filter(user -> user.getHighScore() > 0)
                    .sorted((u1, u2) -> Integer.compare(u2.getHighScore(), u1.getHighScore()))
                    .collect(Collectors.toList());

            // Get top 10 for table display
            List<User> topTenUsers = sortedUsers.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            // Find current user's rank in FULL sorted list
            int currentUserRank = -1;
            if (currentUser != null) {
                for (int i = 0; i < sortedUsers.size(); i++) {
                    if (sortedUsers.get(i).getUsername().equals(currentUser.getUsername())) {
                        currentUserRank = i + 1; // 1-based position
                        break;
                    }
                }
            }

            // Update table with top 10
            leaderboardTable.setItems(FXCollections.observableArrayList(topTenUsers));

            // Update rank display
            if (currentPlayerRank != null) {
                if (currentUser == null) {
                    currentPlayerRank.setText("Silakan login terlebih dahulu.");
                } else if (currentUser.getHighScore() == 0) {
                    currentPlayerRank.setText("Anda belum memiliki skor.");
                } else if (currentUserRank > 0) {
                    currentPlayerRank.setText("Anda berada di posisi #" + currentUserRank);
                } else {
                    currentPlayerRank.setText("Terjadi kesalahan dalam menghitung peringkat.");
                }
            }
        }




        @FXML
        private void goToHome() {
            SceneManager.switchToHome();
        }
    }
