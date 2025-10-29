package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/v1/user")
public class authGetController {

  private UserRepository userRepository;

  public authGetController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/{userId}")
  public ResponseEntity<?> getUser(@PathVariable String userId, Principal principal){
    // principal.getName() is the username authenticated by Spring Security
    if (principal == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    String authUsername = principal.getName();
    // Optional: check that the authenticated user is allowed to access requested userId
    // e.g. if userId should match the username or an id mapping
    // if (!authUsername.equals(userId)) { return ResponseEntity.status(403).body("Forbidden"); }

    // User user = UserStore.getUser(authUsername);

    User user = userRepository.findByUsername(authUsername);

    System.out.println(userId);
    if (user == null) {
      return ResponseEntity.status(404).body("User not found");
    }
    if (Integer.parseInt(userId) != user.getId()) {
      return ResponseEntity.status(401).body("Unauthorized");
    }
    return ResponseEntity.ok(user);
  }
}
