package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Product {

  @Id
  private int id;

  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "Name is required")
  private String name;

  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "Description is required")
  private String description;

  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "Sku is required")
  private String sku;

  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "Manufacturer is required")
  private String manufacturer;

  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "Quantity is required")
  private Integer quantity;

  private long date_added;

  private long date_last_updated;

  private int owner_user_id;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public long getDate_added() {
    return date_added;
  }

  public long getDate_last_updated() {
    return date_last_updated;
  }

  public void setDate_last_updated(long last_updated) {
    this.date_last_updated = last_updated;
  }

  public int getOwner_user_id() {
    return owner_user_id;
  }

  public void setOwner_user_id(int owner_user_id) {
    this.owner_user_id = owner_user_id;
  }

  public void setDate_added(long date_added) {
    this.date_added = date_added;
  }
}
