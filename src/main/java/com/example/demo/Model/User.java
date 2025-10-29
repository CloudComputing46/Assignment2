package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.mindrot.jbcrypt.BCrypt;

@Entity
public class User {

  private int id;

  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "First name is required")
  private String firstName;

  @JsonProperty(access = Access.READ_WRITE)
  @NotBlank(message = "Last name is required")
  private String lastName;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotBlank(message = "Password is required")
  private String password;

  @Id
  @JsonProperty(access = Access.READ_WRITE)
  @NotNull(message = "Username is required")
  private String username;

  private long account_created;
  private long account_updated;

  public User(int identity, String fn, String ln, String p, String u) {
    this.id = identity;
    this.firstName = fn;
    this.lastName = ln;
    this.password = p;
    this.username = u;
    this.account_created = System.currentTimeMillis();
    this.account_updated = System.currentTimeMillis();
  }

  public User() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUsername() {
    return username;
  }

  public void setAccount_updated(long account_updated) {
    this.account_updated = account_updated;
  }

  public long getAccount_updated() {
    return account_updated;
  }

  public long getAccount_created() {
    return account_created;
  }

  public boolean verifyPassword(String checkPassword) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.matches(checkPassword, this.password);
  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (password == null) {
      System.out.println("Password is null");
      return;
    }

    if (!(password.startsWith("$2a$") || password.startsWith("$2b$")
        || password.startsWith("$2y$"))) {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      password = encoder.encode(password);
    }

    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

}
