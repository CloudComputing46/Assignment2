package com.example.demo;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Bean
//	CommandLineRunner runner(UserRepository repo) {
//		return args -> {
//			User u = new User();
//			u.setUsername("quicktest");
//			u.setPassword("password");
//			u.setFirstName("quicktestFirstname");
//			u.setLastName("quicktestLastname");
//			repo.save(u); // if this works, DB is fine
//			System.out.println("saved test user");
//		};
//	}

}
