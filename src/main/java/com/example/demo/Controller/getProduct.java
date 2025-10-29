package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/product")
public class getProduct {
  ProductRepository productRepository;

  public getProduct(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping("/{productId}")
  public ResponseEntity<?> getProductFromMap(@PathVariable String productId) {
    System.out.println("Product ID is : ");
    System.out.println(productId);
    if (productId == null) {
      return ResponseEntity.status(403).build();
    }

//    Product currProduct = ProductStore.getProduct(Integer.parseInt(productId));
    Product currProduct = productRepository.findById(Integer.parseInt(productId));

    if (currProduct == null) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.status(200).body(currProduct);
  }
}
