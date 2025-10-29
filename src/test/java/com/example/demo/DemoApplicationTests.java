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
class DemoApplicationTests {

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
		User actualObject = new User();
		actualObject.setUsername("bob@gmail.com");
		actualObject.setFirstName("Bob");
		actualObject.setLastName("Martin");
		actualObject.setPassword("password");


		String stringInput = """
		{
			"username": "bob@gmail.com",
			"firstName": "Bob",
			"lastName": "Martin",
			"password": "password"
		}
		""";


		// whenever userRepository.save() is called, it returns u, mocking a db
		when(userRepository.save(any(User.class))).thenReturn(actualObject);

		given()
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(stringInput)
				.when()
					.post("/v1/user")
				.then()
					.statusCode(201)
					.body("id", equalTo(actualObject.getId()))
					.body("username", equalTo(actualObject.getUsername()))
					.body("firstName", equalTo(actualObject.getFirstName()))
					.body("lastName", equalTo(actualObject.getLastName()));
	}

	@Test
	void getProductNoAuthTest() throws Exception {
		Product inputProduct = new Product();
		inputProduct.setId(1);
		inputProduct.setDescription("testing the get product endpoint");
		inputProduct.setName("Testing product");
		inputProduct.setQuantity(2);
		inputProduct.setSku("idk what sku means");
		inputProduct.setManufacturer("Staples");

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
	void createUserBadRequestNoAuthTest() throws Exception {
		User actualObject = new User();
		actualObject.setUsername("bob@gmail.com");
		actualObject.setFirstName("Bob");
		actualObject.setLastName("Martin");
		actualObject.setPassword("password");

		// check case where username is NOT an email
		// input has missing fields
		String stringInput = """
				{
				"username" : "bob@gmail.com"
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
		User actualObject = new User();
		actualObject.setUsername("bob@gmail.com");
		actualObject.setFirstName("Bob");
		actualObject.setLastName("Martin");
		actualObject.setPassword("password");

		String stringInput = """
				{
				"username": "bob123",
				"firstName": "Bob",
				"lastName": "Martin",
				"password": "password"
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
		Product inputProduct = new Product();
		inputProduct.setId(1);
		inputProduct.setDescription("testing the get product endpoint");
		inputProduct.setName("Testing product");
		inputProduct.setQuantity(2);
		inputProduct.setSku("idk what sku means");
		inputProduct.setManufacturer("Staples");


		when(productRepository.findById(1)).thenReturn(inputProduct);

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(inputProduct)
				.when()
				.get("/v1/product/{productId}", 2)
				.then()
				.statusCode(401);
	}
	

}
