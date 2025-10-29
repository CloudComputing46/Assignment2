package com.example.demo.Repository;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>{
  Product findById(int productId);
}