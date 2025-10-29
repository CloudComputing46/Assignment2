package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Model.UserStore;
import com.example.demo.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping("/v1/user")
public class authPutUserController {

  private UserRepository userRepository;

  public authPutUserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PutMapping("/{userId}")
  public ResponseEntity<?> modifyUser(@Valid @RequestBody User newUser, @PathVariable String userId,
    Principal principal) {
    // should handle auth
    if (principal == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    User currentUser = userRepository.findByUsername(principal.getName());

    if (currentUser == null) {
      return ResponseEntity.status(400).body("User not found");
    }

    userRepository.delete(currentUser);

    currentUser.setUsername(newUser.getUsername());
    currentUser.setPassword(newUser.getPassword());
    currentUser.setFirstName(newUser.getFirstName());
    currentUser.setLastName(newUser.getLastName());
    currentUser.setAccount_updated(System.currentTimeMillis());

    userRepository.save(currentUser);

    return ResponseEntity.status(200).body(currentUser);
  }
}
