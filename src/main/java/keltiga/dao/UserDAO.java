package keltiga.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import keltiga.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static final String USER_FILE = "data/users.json";
    private Map<String, User> users = new HashMap<>();

    public UserDAO() {
        // Buat folder data jika belum ada
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            System.out.println("Error creating data directory: " + e.getMessage());
        }
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
        Path userFile = Paths.get(USER_FILE);
        if (!Files.exists(userFile)) {
            try {
                // Buat file kosong jika belum ada
                Files.createDirectories(userFile.getParent());
                Files.write(userFile, "{}".getBytes());
                users = new HashMap<>();
            } catch (IOException e) {
                System.out.println("Error creating users file: " + e.getMessage());
            }
            return;
        }

        try (Reader reader = new FileReader(USER_FILE)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            users = gson.fromJson(reader, type);
            if (users == null) {
                users = new HashMap<>();
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            users = new HashMap<>();
        }
    }

    public Map<String, User> getAllUsers() {
        return users;
    }
}
