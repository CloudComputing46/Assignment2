package com.example.demo;

import com.example.demo.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private UserRepository userRepository;

  public SecurityConfig(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Defines the security filter chain.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Disable CSRF for basic auth
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/v1/user").permitAll()
            .requestMatchers(HttpMethod.GET, "/healthz").permitAll()
            .requestMatchers(HttpMethod.GET, "/v1/product/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(httpBasic -> {});

    return http.build();
  }

  /**
   * Custom UserDetailsService to load user details from the UserStore.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      com.example.demo.Model.User user = userRepository.findByUsername(username);

      if (user == null) {
        throw new UsernameNotFoundException("User not found: " + username);
      }

      System.out.println("DEBUG: username=" + user.getUsername());
      System.out.println("DEBUG: stored password='" + user.getPassword() + "'");
      System.out.println("DEBUG: length=" + user.getPassword().length());

      // Return a Spring Security UserDetails object, using the HASHED password.
      return User.builder()
          .username(user.getUsername())
          .password(user.getPassword()) // Now works because of the new getter
          .roles("USER")
          .build();
    };
  }

  /**
   * Defines the BCryptPasswordEncoder bean used by Spring Security for comparison.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    // This encoder will compare the password from the request against the hash
    // retrieved by the UserDetailsService.
    return new BCryptPasswordEncoder();
  }
}






