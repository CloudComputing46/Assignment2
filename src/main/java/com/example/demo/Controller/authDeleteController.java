package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Model.ProductStore;
import com.example.demo.Model.User;
import com.example.demo.Model.UserStore;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/product")
public class authDeleteController {
  UserRepository userRepository;
  ProductRepository productRepository;

  public authDeleteController(UserRepository userRepository, ProductRepository productRepository) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<?> deleteProduct(@PathVariable String productId, Principal principal) {
    if (productId == null) {
      return ResponseEntity.status(400).body("Bad Request");
    }
    if (principal == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

//    User currUser = UserStore.getUser(principal.getName());
    User currUser = userRepository.findByUsername(principal.getName());

//    Product currProduct = ProductStore.getProduct(Integer.parseInt(productId));
    Product currProduct = productRepository.findById(Integer.parseInt(productId));

    if (currProduct == null) {
      return ResponseEntity.status(404).body("Not found");
    }

    if (currProduct.getOwner_user_id() != currUser.getId()) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    productRepository.delete(currProduct);

    return ResponseEntity.status(200).body("Deleted");
  }
}
