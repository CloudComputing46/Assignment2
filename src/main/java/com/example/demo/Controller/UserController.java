package com.example.demo.Controller;

import com.example.demo.Repository.UserRepository;
import jakarta.validation.Valid;
import java.util.*;

import com.example.demo.Model.User;
import java.util.Map;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1/user")
public class UserController {
  private UserRepository userRepository;
  private static AtomicInteger id = new AtomicInteger(1);

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping
  public ResponseEntity<?> createUser(@Valid @RequestBody User newUser, BindingResult br) {

    if (br.hasErrors()) {
      Map<String, Object> errors = new HashMap<>();
      for (FieldError fe : br.getFieldErrors()) {
        System.out.printf("field=%s, rejected=%s, msg=%s%n",
            fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage());
        errors.put(fe.getField(), fe.getDefaultMessage());
      }
      return ResponseEntity.badRequest().body(errors);
    }

    int newId = id.getAndIncrement();

    // verifying if the username is an email
    if (!newUser.getUsername().contains("@") || !newUser.getUsername().contains(".com")) {
      return ResponseEntity.status(400).body("Username must be a valid email address");
    }


    newUser.setPassword(newUser.getPassword());
    newUser.setId(newId);

    userRepository.save(newUser);

    return ResponseEntity.status(201).body(userRepository.findByUsername(newUser.getUsername()));
  }
}
