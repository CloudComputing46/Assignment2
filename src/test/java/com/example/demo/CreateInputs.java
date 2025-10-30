package com.example.demo;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;

public class CreateInputs {
  protected User createUser(String username, String password, int id) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setFirstName("Bob");
    user.setLastName("Martin");
    user.setPassword(password);

    return user;
  }

  protected Product createProduct() {
    Product inputProduct = new Product();
    inputProduct.setId(1);
    inputProduct.setDescription("testing the get product endpoint");
    inputProduct.setName("Testing product");
    inputProduct.setQuantity(2);
    inputProduct.setSku("idk what sku means");
    inputProduct.setManufacturer("Staples");
    inputProduct.setOwner_user_id(1);

    return inputProduct;
  }
}
