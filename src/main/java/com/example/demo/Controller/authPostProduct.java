package com.example.demo.Controller;


import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/product")
public class authPostProduct {
  private ProductRepository productRepository;
  private UserRepository userRepository;
  private static AtomicInteger id = new AtomicInteger(1);

  public authPostProduct(ProductRepository productRepository, UserRepository userRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  @PostMapping
  public ResponseEntity<?> postProduct(Principal principal, @Valid @RequestBody Product product){
    if (principal == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    User owner = userRepository.findByUsername(principal.getName());

    product.setId(id.getAndIncrement());
    product.setOwner_user_id(owner.getId());
    product.setDate_added(System.currentTimeMillis());
    product.setDate_last_updated(System.currentTimeMillis());

    //ProductStore.addProduct(product);
    productRepository.save(product);

    return ResponseEntity.status(201).body(product);

  }
}
