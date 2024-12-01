package keltiga.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import keltiga.dao.UserDAO;
import keltiga.model.User;

public class LeaderboardController {

    @FXML private TableView<User> leaderboardTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, Integer> highScoreColumn;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        highScoreColumn.setCellValueFactory(new PropertyValueFactory<>("highScore"));
        loadLeaderboard();
    }

    // Load the leaderboard data
    private void loadLeaderboard() {
        Map<String, User> usersMap = userDAO.getAllUsers();
        List<User> userList = usersMap.values().stream().collect(Collectors.toList());

        ObservableList<User> observableUserList = FXCollections.observableArrayList(userList);
        leaderboardTable.setItems(observableUserList);  // Set leaderboard data
    }

    @FXML
    private void goToLevelSelection() {
        SceneManager.switchToHome();
    }
}