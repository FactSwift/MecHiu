package keltiga.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import keltiga.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static final String USER_FILE = "data/users.json";
    private Map<String, User> users = new HashMap<>();

    public UserDAO() {
        loadUsers();
    }

    public void saveUser(User user) {
        users.put(user.getUsername(), user);
        saveUsers();
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void deleteUser(String username) {
        users.remove(username);
        saveUsers();  // Save the updated users list to the file
    }

    public void saveUsers() {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(USER_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        if (!Files.exists(Paths.get(USER_FILE))) {
            return; // If no file, start with empty users
        }
        try (Reader reader = new FileReader(USER_FILE)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            users = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, User> getAllUsers() {
        return users;
    }
}
