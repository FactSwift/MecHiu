package keltiga.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import keltiga.model.User;
import keltiga.dao.UserDAO;
import java.util.List;     // Import List

public class LeaderboardController {

    @FXML private TableView<User> leaderboardTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, Integer> highScoreColumn;

    private User currentUser;


    public void setCurrentUser(User user) {
        this.currentUser = user;

        System.out.println("Current user set to: " + user.getUsername());
    }


    @FXML
    public void initialize() {
        loadLeaderboard();
    }


    private void loadLeaderboard() {
        UserDAO userDAO = new UserDAO();

        List<User> users = userDAO.getAllUsers();


        ObservableList<User> observableUserList = FXCollections.observableArrayList(users);


        leaderboardTable.setItems(observableUserList);
    }


    @FXML
    private void goToLevelSelection() {
        SceneManager.switchToLevelSelection();
    }
}
