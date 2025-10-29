package com.example.demo.Model;

import java.util.*;

public class UserStore {
  private static Map<String, User> userStore = new HashMap<>();

  public static void addUser(String username, User newUser) {
    userStore.put(username, newUser);
  }

  public static boolean validateUser(String username, String password) {
    if (userStore.containsKey(username)) {
      if (userStore.get(username).verifyPassword(password)) {
        return true;
      }
    }
    return false;
  }

  public static User getUser(String username) {
    return userStore.get(username);
  }
}

