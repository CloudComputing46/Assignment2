package com.example.demo;

import static io.restassured.specification.ProxySpecification.port;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static io.restassured.RestAssured.*;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NegativeTestingClass extends CreateInputs {

  @LocalServerPort
  private int port;

  @MockitoBean
  UserRepository userRepository;

  @MockitoBean
  ProductRepository productRepository;

  private String user = "cmonger@gmail.com";
  private String pass = "cmonger";

  @BeforeEach
  public void setUp() {
    port(port);
    baseURI = "http://localhost:" + port;
  }

  @Test
  void createUserBadRequestNoAuthTest() throws Exception {
    User actualObject = createUser(this.user, this.pass, 1);

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
    User actualObject = createUser(this.user, this.pass, 1);

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
    User user = createUser("cm@gmail.com", this.pass, 1);

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
    User user = createUser("cmonger", this.pass, 1);

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
    User user = createUser(this.user, this.pass, 1);

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
    User user = createUser(this.user, this.pass, 1);

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
}
