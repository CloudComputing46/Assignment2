package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/product")
public class authPatchProductController {
  UserRepository userRepository;
  ProductRepository productRepository;

  public authPatchProductController(UserRepository userRepository, ProductRepository productRepository) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @PatchMapping("/{productId}")
  public ResponseEntity<?> patchUser(Principal principal, @PathVariable String productId,
      @RequestBody Map<String, Object> input) {
    if (principal == null) {
      return ResponseEntity.status(400).body("Bad Request");
    }
    if (input == null) {
      return ResponseEntity.status(204).body("No content");
    }

    // User currUser = UserStore.getUser(principal.getName());
    User currUser = userRepository.findByUsername(principal.getName());

    if (currUser == null) {
      return ResponseEntity.status(400).body("Bad Request");
    }

    // Product currProduct = ProductStore.getProduct(Integer.parseInt(productId));
    Product currProduct = productRepository.findById(Integer.parseInt(productId));

    if (currProduct == null) {
      return ResponseEntity.status(400).body("Bad Request");
    }

    if (currUser.getId() != currProduct.getOwner_user_id()) {
      return ResponseEntity.status(403).body("Forbidden");
    }

    productRepository.delete(currProduct);

    for (Map.Entry<String, Object> entry : input.entrySet()) {
      String param = entry.getKey();
      if (param.equals("name")) {
        currProduct.setName(entry.getValue().toString());
      }
      else if (param.equals("description")) {
        currProduct.setDescription(entry.getValue().toString());
      }
      else if (param.equals("sku")) {
        currProduct.setSku(entry.getValue().toString());
      }
      else if (param.equals("manufacturer")) {
        currProduct.setManufacturer(entry.getValue().toString());
      }
      else if (param.equals("quantity")) {
        currProduct.setQuantity(Integer.parseInt(entry.getValue().toString()));
      }
    }

    productRepository.save(currProduct);

    return ResponseEntity.status(204).body(currProduct);
  }
}
