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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PositiveTestingClass extends CreateInputs {

  @LocalServerPort
  private int port;

  @MockitoBean
  UserRepository userRepository;

  @MockitoBean
  ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    port(port);
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
    User actualObject = createUser(this.user, this.pass, 1);


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
    User user = createUser(this.user, this.pass, 1);

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

    User user = createUser(this.user, this.pass, 1);

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
}
