package com.example.demo;

import static io.restassured.specification.ProxySpecification.port;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static io.restassured.RestAssured.*;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NegativeTest extends CreateInputs {

  @LocalServerPort
  private int port;

  @MockitoBean
  UserRepository userRepository;

  @MockitoBean
  ProductRepository productRepository;



  @BeforeEach
  public void setUp() {
    port(port);
    baseURI = "http://localhost:" + port;
  }

  @Test
  void createUserBadRequestNoAuthTest() throws Exception {
    User actualObject = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    // check case where username is NOT an email
    // input has missing fields
    String stringInput = """
				{
				"username" : "cmonger@gmail.com"
				}
				""";

    when(userRepository.save(any(User.class))).thenReturn(actualObject);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(stringInput)
        .when()
        .post("/v1/user")
        .then()
        .statusCode(400);
  }

  @Test
  void createUserInvalidUsernameNoAuthTest() throws Exception {
    User actualObject = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    String stringInput = """
				{
				"username": "cmonger123",
				"firstName": "Bob",
				"lastName": "Martin",
				"password": "cmonger"
				}
				""";

    when(userRepository.save(any(User.class))).thenReturn(actualObject);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(stringInput)
        .when()
        .post("/v1/user")
        .then()
        .statusCode(400);
  }

  @Test
  void getProductInvalidIdNoAuthTest() throws Exception {
    Product inputProduct = createProduct();


    when(productRepository.findById(1)).thenReturn(inputProduct);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(inputProduct)
        .when()
        .get("/v1/product/{productId}", 2)
        .then()
        .statusCode(401);
  }

  @Test
  void getUserInvlidUsernameAuthTest() throws Exception {
    User user = createUser("cm@gmail.com", this.pass, 1, this.firstName,
        this.lastName);

    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .when()
        .get("/v1/user/{userId}", 1)
        .then()
        .log().all()
        .statusCode(401);
  }

  @Test
  void getUserInvlidUsernameNotEmailAuthTest() throws Exception {
    User user = createUser("cmonger", this.pass, 1, this.firstName,
    this.lastName);

    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .when()
        .get("/v1/user/{userId}", 1)
        .then()
        .log().all()
        .statusCode(401);
  }

  @Test
  void getUserInvlidPasswordAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, "abcd")
        .when()
        .get("/v1/user/{userId}", 1)
        .then()
        .log().all()
        .statusCode(401);
  }

  @Test
  void getUserInvlidIdAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .when()
        .get("/v1/user/{userId}", 2)
        .then()
        .log().all()
        .statusCode(401);
  }

  @Test
  void createProductInvalidUsernameAuthTest() throws Exception {
    Product product = createProduct();
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    when(productRepository.findById(1)).thenReturn(product);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
    .auth()
        .preemptive()
        .basic("cmonger123", this.pass)
        .when()
        .post("/v1/product")
        .then()
        .statusCode(401);
  }

  @Test
  void createProductInvalidPasswordAuthTest() throws Exception {
    Product product = createProduct();
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    when(productRepository.findById(1)).thenReturn(product);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, "abcd")
        .when()
        .post("/v1/product")
        .then()
        .statusCode(401);
  }

  @Test
  void putUserNewUsernameAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);
    User updatedUser = createUser("cmonger123", this.pass,
        1, this.firstName, this.lastName);

    String updatedUserString = """
        {
        "id": 1,
        "username": "cmonger123",
        "firstName": "Charlie",
        "lastName": "Monger",
        "password": "cmonger"
        }
        """;

    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(userRepository.save(updatedUser)).thenReturn(updatedUser);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(updatedUserString)
        .when()
        .put("/v1/user/{userId}", 1)
        .then()
        .log().all()
        .statusCode(400);
  }

  @Test
  void putProductWrongProductIdAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);
    Product product = createProduct();

    Product newProduct = createProduct("New product name", this.description, this.sku,
        this.manufacturer, 3, 1);

    String newProductString = """
        {
          "name": "New product name",
          "description": "new desc",
          "sku": "idk what sku is",
          "manufacturer": "Staples for some reason",
          "quantity": 3
        }
        """;

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(productRepository.save(newProduct)).thenReturn(newProduct);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newProductString)
        .when()
        .put("/v1/product/{productId}", 3)
        .then()
        .log().all()
        .statusCode(400);
  }

  @Test
  void putProductWrongOwnerIdAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);
    Product product = createProduct(this.productName, this.description, this.sku,
        this.manufacturer, this.quantity, 2);

    Product newProduct = createProduct("New product name", this.description, this.sku,
        this.manufacturer, 3, 2);

    String newProductString = """
        {
          "name": "New product name",
          "description": "new desc",
          "sku": "idk what sku is",
          "manufacturer": "Staples for some reason",
          "quantity": 3
        }
        """;

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(productRepository.save(newProduct)).thenReturn(newProduct);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newProductString)
        .when()
        .put("/v1/product/{productId}", 1)
        .then()
        .log().all()
        .statusCode(403);
  }

  @Test
  void patchProductWrongProductIdAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);
    Product product = createProduct();

    Product newProduct = createProduct("New product name", this.description, this.sku,
        this.manufacturer, 3, 1);

    String newProductString = """
        {
          "name": "New product name"
        }
        """;

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(productRepository.save(newProduct)).thenReturn(newProduct);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newProductString)
        .when()
        .patch("/v1/product/{productId}", 3)
        .then()
        .log().all()
        .statusCode(400);
  }

  @Test
  void patchProductWrongOwnerIdAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);
    Product product = createProduct(this.productName, this.description, this.sku,
        this.manufacturer, this.quantity, 2);

    Product newProduct = createProduct("New product name", this.description, this.sku,
        this.manufacturer, 3, 2);

    String newProductString = """
        {
          "name": "New product name"
        }
        """;

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(productRepository.save(newProduct)).thenReturn(newProduct);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newProductString)
        .when()
        .patch("/v1/product/{productId}", 1)
        .then()
        .log().all()
        .statusCode(403);
  }

  @Test
  void deleteProductWrongUserIdAuthTest() throws Exception {
    Product product = createProduct();

    User user = createUser(this.user, this.pass, 2, this.firstName, this.lastName);

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    doNothing().when(productRepository).delete(product);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(product)
        .when()
        .delete("/v1/product/{productId}", 1)
        .then()
        .log().all()
        .statusCode(401);
  }

  @Test
  void deleteProductWrongProductIdAuthTest() throws Exception {
    Product product = createProduct();

    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    doNothing().when(productRepository).delete(product);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(product)
        .when()
        .delete("/v1/product/{productId}", 2)
        .then()
        .log().all()
        .statusCode(404);
  }
}

