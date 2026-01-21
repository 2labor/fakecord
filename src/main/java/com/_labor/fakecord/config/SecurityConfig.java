package com._labor.fakecord.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.TokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

  //password encoder
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception {
    http 
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .sessionManagement(session -> 
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css").permitAll()
        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
        .anyRequest().authenticated()
      );

    http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

    http.exceptionHandling(exception -> exception.
      authenticationEntryPoint((request, response, authException) -> {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized: Please log in\"}");
      })
    );

      return http.build();
  } 

  @Bean
  public UserDetailsService userDetailsService(AccountRepository repository) {
    return username -> repository.findByLogin(username)
      .map(account -> User
        .withUsername(account.getLogin())
        .password(account.getPassword())
        .authorities("ROLE_USER")
        .build()
      )
      .orElseThrow(() -> new UsernameNotFoundException("User name: " + username));
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
