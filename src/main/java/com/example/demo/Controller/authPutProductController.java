package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/product")
public class authPutProductController {
  ProductRepository productRepository;
  UserRepository userRepository;

  public authPutProductController(ProductRepository productRepository, UserRepository userRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  @PutMapping("/{productId}")
  public ResponseEntity<?> updateProduct(Principal principal, @PathVariable String productId,
      @Valid @RequestBody Product product) {
    if (principal == null) {
      return ResponseEntity.status(400).body("Bad Request");
    }
    if (product == null) {
      return ResponseEntity.status(204).body("No content");
    }

    // Product currProduct = ProductStore.getProduct(Integer.parseInt(productId));
    Product currProduct = productRepository.findById(Integer.parseInt(productId));

    if (currProduct == null) {
      return ResponseEntity.status(400).body("Bad Request");
    }

    // User currUser = UserStore.getUser(principal.getName());
    User currUser = userRepository.findByUsername(principal.getName());

    if (currUser.getId() != currProduct.getOwner_user_id()) {
      return ResponseEntity.status(403).body("Forbidden");
    }

    productRepository.delete(currProduct);

    currProduct.setName(product.getName());
    currProduct.setDescription(product.getDescription());
    currProduct.setSku(product.getSku());
    currProduct.setManufacturer(product.getManufacturer());
    currProduct.setQuantity(product.getQuantity());
    currProduct.setDate_last_updated(System.currentTimeMillis());

    productRepository.save(currProduct);

    return ResponseEntity.status(200).body(currProduct);
  }
}
