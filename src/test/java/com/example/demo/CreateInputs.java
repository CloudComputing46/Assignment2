package com.example.demo;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;

public class CreateInputs {
  protected String user = "cmonger@gmail.com";
  protected String pass = "cmonger";
  protected String firstName = "Charlie";
  protected String lastName = "Monger";

  protected String productName = "Testing product";
  protected String description = "testing the get product endpoint";
  protected String sku = "idk what sku means";
  protected String manufacturer = "Staples";
  protected int quantity = 2;
  protected int ownerId = 1;

  protected User createUser(String username, String password, int id, String firstName,
      String lastName) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setPassword(password);

    return user;
  }

  protected Product createProduct() {
    Product product = new Product();
    product.setId(1);
    product.setName(productName);
    product.setDescription(description);
    product.setManufacturer(manufacturer);
    product.setQuantity(quantity);
    product.setSku(sku);
    product.setManufacturer(manufacturer);
    product.setOwner_user_id(ownerId);
    return product;
  }

  protected Product createProduct(String prodName, String desc, String skuLocal, String manufac,
      int quant, int ownId) {
    Product inputProduct = new Product();
    inputProduct.setId(1);
    inputProduct.setDescription(desc);
    inputProduct.setName(prodName);
    inputProduct.setQuantity(quant);
    inputProduct.setSku(skuLocal);
    inputProduct.setManufacturer(manufac);
    inputProduct.setOwner_user_id(ownId);

    return inputProduct;
  }
}
