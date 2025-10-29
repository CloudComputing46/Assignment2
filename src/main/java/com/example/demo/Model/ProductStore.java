package com.example.demo.Model;

import java.util.HashMap;
import java.util.Map;

public class ProductStore {
  private static Map<Integer, Product> products = new HashMap<>();

  public static void addProduct(Product product) {
    products.put(product.getId(), product);
  }

  public static Product getProduct(int id) {
    return products.get(id);
  }

  public static void removeProduct(int id) {
    products.remove(id);
  }
}
