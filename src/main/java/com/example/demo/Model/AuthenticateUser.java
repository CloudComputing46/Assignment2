package com.example.demo.Model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthenticateUser {
  private static boolean isAuthenticated = false;
  private static String authUsername;

  public static boolean isAuthenticated() {
    return isAuthenticated;
  }

  public static String getAuthUsername() {
    if (isAuthenticated) {
      return authUsername;
    }
    return null;
  }

  public static boolean authenticate(String token) {
    token = token.substring("Basic ".length());
    byte[] decodedBytes;
    try {
      decodedBytes = Base64.getDecoder().decode(token);
    } catch (IllegalArgumentException e) {
      return false;
    }

    String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
    String[] values = credentials.split(":", 2);

    if (values.length != 2) {
      return false;
    }

    String username = values[0];
    String password = values[1];

    if (UserStore.validateUser(username, password)) {
      isAuthenticated = true;
      authUsername = username;
      return true;
    }
    return false;
  }
}
