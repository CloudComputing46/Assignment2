package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	UserRepository userRepository;

	@MockitoBean
	ProductRepository productRepository;


	@Test
	void checkHealthNoAuthTest() throws Exception {
		mockMvc.perform(get("/healthz"))
				.andExpect(status().isOk());
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

		mockMvc.perform(post("/v1/user")
						.contentType(MediaType.APPLICATION_JSON)
						.content(stringInput))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("bob@gmail.com"))
				.andExpect(jsonPath("$.firstName").value("Bob"))
				.andExpect(jsonPath("$.lastName").value("Martin"));
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

		mockMvc.perform(get("/v1/product/{productId}", inputProduct.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Testing product"))
				.andExpect(jsonPath("$.description").value("testing the get product endpoint"))
				.andExpect(jsonPath("$.sku").value("idk what sku means"))
				.andExpect(jsonPath("$.manufacturer").value("Staples"))
				.andExpect(jsonPath("$.quantity").value(2));
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

		mockMvc.perform(post("/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(stringInput))
				.andExpect(status().isBadRequest());
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

		mockMvc.perform(post("/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(stringInput))
				.andExpect(status().isBadRequest());
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

		mockMvc.perform(get("/v1/product/2"))
				.andExpect(status().isUnauthorized());
	}
	

}
