package com.example.demo;

import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static io.restassured.RestAssured.*;



import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class PositiveTest extends CreateInputs {


  @LocalServerPort
  private int port;

  @MockitoBean
  UserRepository userRepository;

  @MockitoBean
  ProductRepository productRepository;

  @BeforeEach
  void setUp() {
//    port(port);
    RestAssured.port = port;
    baseURI = "http://localhost:" + port;
  }

  private String user = "cmonger@gmail.com";
  private String pass = "cmonger";


  @Test
  void checkHealthNoAuthTest() throws Exception {
    given()
        .when()
        .get("/healthz")
        .then()
        .statusCode(200);
  }

  @Test
  void createUserNoAuthTest() throws Exception {
    User actualObject = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);


    String stringInput = """
		{
		  "id": 1,
			"username": "cmonger@gmail.com",
			"firstName": "Bob",
			"lastName": "Martin",
			"password": "cmonger"
		}
		""";


    // whenever userRepository.save() is called, it returns u, mocking a db
    when(userRepository.save(any(User.class))).thenReturn(actualObject);
    when(userRepository.findByUsername(actualObject.getUsername())).thenReturn(actualObject);

    given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(stringInput)
        .when()
          .post("/v1/user")
        .then()
          .log().all()
          .statusCode(201)
          .body("id", equalTo(actualObject.getId()))
          .body("username", equalTo(actualObject.getUsername()))
          .body("firstName", equalTo(actualObject.getFirstName()))
          .body("lastName", equalTo(actualObject.getLastName()));
  }

  @Test
  void getProductNoAuthTest() throws Exception {
    Product inputProduct = createProduct();

    when(productRepository.findById(1)).thenReturn(inputProduct);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(inputProduct)
        .when()
        .get("/v1/product/{productId}", 1)
        .then()
        .statusCode(200)
        .body("id", equalTo(inputProduct.getId()))
        .body("description", equalTo(inputProduct.getDescription()))
        .body("name", equalTo(inputProduct.getName()))
        .body("sku", equalTo(inputProduct.getSku()))
        .body("manufacturer", equalTo(inputProduct.getManufacturer()))
        .body("quantity", equalTo(inputProduct.getQuantity()));
  }

  @Test
  void getUserAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
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
        .statusCode(200)
        .body("id", equalTo(1))
        .body("username", equalTo(user.getUsername()))
        .body("firstName", equalTo(user.getFirstName()))
        .body("lastName", equalTo(user.getLastName()));
  }

  @Test
  void createProductAuthTest() throws Exception {
    Product inputProduct = createProduct();

    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);

    when(productRepository.save(any(Product.class))).thenReturn(inputProduct);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
        .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(inputProduct)
        .when()
        .post("/v1/product")
        .then()
        .log().all()
        .statusCode(201)
        .body("id", equalTo(inputProduct.getId()))
        .body("description", equalTo(inputProduct.getDescription()))
        .body("name", equalTo(inputProduct.getName()))
        .body("sku", equalTo(inputProduct.getSku()))
        .body("manufacturer", equalTo(inputProduct.getManufacturer()))
        .body("quantity", equalTo(inputProduct.getQuantity()));
  }

  @Test
  void putUserNewUsernameAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);
    User updatedUser = createUser("cmonger123@gmail.com", this.pass,
        1, this.firstName, this.lastName);

    String updatedUserString = """
        {
        "id": 1,
        "username": "cmonger123@gmail.com",
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
        .statusCode(204);
  }

  @Test
  void putUserNewPasswordAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);
    User updatedUser = createUser(this.user, "cmonger123",
        1, this.firstName, this.lastName);

    String updatedUserString = """
        {
        "id": 1,
        "username": "cmonger@gmail.com",
        "firstName": "Charlie",
        "lastName": "Monger",
        "password": "cmonger123"
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
        .statusCode(204);
  }

  @Test
  void putUserNewFirstnameAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);
    User updatedUser = createUser(this.user, this.pass,
        1, "Reginald", this.lastName);

    String updatedUserString = """
        {
        "id": 1,
        "username": "cmonger123@gmail.com",
        "firstName": "Reginald",
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
        .statusCode(204);
  }

  @Test
  void putUserNewLastnameAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName,
        this.lastName);
    User updatedUser = createUser(this.user, this.pass,
        1, this.firstName, "Spencer");

    String updatedUserString = """
        {
        "id": 1,
        "username": "cmonger123@gmail.com",
        "firstName": "Charlie",
        "lastName": "Spencer",
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
        .statusCode(204);
  }

  @Test
  void putProductAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);
    Product product = createProduct();

    Product newProduct = createProduct("New product name", this.description, this.sku,
        this.manufacturer, 3, 1);

    when(productRepository.findById(product.getId())).thenReturn(product);
    when(productRepository.save(newProduct)).thenReturn(newProduct);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

    given()
    .auth()
        .preemptive()
        .basic(this.user, this.pass)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newProduct)
        .when()
        .put("/v1/product/{productId}", 1)
        .then()
        .log().all()
        .statusCode(204);
  }

  @Test
  void patchProductAuthTest() throws Exception {
    User user = createUser(this.user, this.pass, 1, this.firstName, this.lastName);
    Product product = createProduct();

    Product newProduct = createProduct("New product name", this.description, this.sku,
        this.manufacturer, this.quantity, this.ownerId);

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
        .statusCode(204);
  }

  @Test
  void deleteProductAuthTest() throws Exception {
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
        .delete("/v1/product/{productId}", 1)
        .then()
        .log().all()
        .statusCode(204);
  }
}
