package com.example.demo.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class healthCheckController {
  @GetMapping("/healthz")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("Application is healthy and running");
  }
}
