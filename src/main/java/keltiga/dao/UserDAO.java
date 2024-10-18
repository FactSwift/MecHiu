package keltiga.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import keltiga.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static final String OUTPUT_FILE = "users.json";
    private Map<String, User> users = new HashMap<>();

    public UserDAO() {
        loadUsers();
    }


    public void saveUser(User user) {
        users.put(user.getUsername(), user);
        saveUsers();
    }


    private void loadUsers() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("users.json");

        if (inputStream == null) {
            System.out.println("users.json not found in resources. Initializing with an empty user database.");
            users = new HashMap<>();
            return;
        }


        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(inputStream)) {
            Type userMapType = new TypeToken<Map<String, User>>() {}.getType();
            users = gson.fromJson(reader, userMapType);

            if (users == null) {
                users = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            users = new HashMap<>();
        }
    }


    private void saveUsers() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            gson.toJson(users, writer);  // Write users map to file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public User getUser(String username) {
        return users.get(username);
    }
}
