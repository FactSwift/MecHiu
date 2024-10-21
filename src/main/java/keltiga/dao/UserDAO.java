package keltiga.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import keltiga.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;     
import java.util.ArrayList;

public class UserDAO {
    private static final String WORKING_DIRECTORY = "data";
    private static final String DATABASE_FILE = WORKING_DIRECTORY + "/users.json";
    private static final String DEFAULT_RESOURCE_FILE = "/users.json";

    private Map<String, User> users = new HashMap<>();

    public UserDAO() {
        loadUsers();
    }


    public void saveUser(User user) {
        users.put(user.getUsername(), user);
        saveUsers();
    }


    private void loadUsers() {
        Path workingDir = Paths.get(WORKING_DIRECTORY);
        if (!Files.exists(workingDir)) {
            try {
                Files.createDirectories(workingDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(DATABASE_FILE);

        if (file.exists() && file.length() > 0) {
            System.out.println("Reading users.json from writable directory: " + DATABASE_FILE);
            readFromFile(file);
        } else {
            System.out.println("Writable users.json not found, reading default users.json from resources.");
            InputStream inputStream = getClass().getResourceAsStream(DEFAULT_RESOURCE_FILE);
            if (inputStream != null) {
                readFromStream(inputStream);
                saveUsers();
            } else {
                System.out.println("Default users.json not found. Initializing empty user database.");
                users = new HashMap<>();
            }
        }
    }


    private void readFromFile(File file) {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
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


    private void readFromStream(InputStream inputStream) {
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
        try (FileWriter writer = new FileWriter(DATABASE_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public User getUser(String username) {
        return users.get(username);
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
