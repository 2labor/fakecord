package com._labor.fakecord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

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
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.configurationSource(request -> {
          var opt = new CorsConfiguration();
          opt.setAllowedOrigins(java.util.List.of("*"));
          opt.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
          opt.setAllowedHeaders(java.util.List.of("*"));
          return opt;
        }))
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // disable session 
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css").permitAll()
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/ws-chat/**").permitAll()
        .anyRequest().authenticated()
      )

      .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

      return http.build();
  } 

  @Bean
  public UserDetailsService userDetailsService(AccountRepository repository) {
    return username -> repository.findByLogin(username)
      .map(account -> User
        .withUsername(account.getLogin())
        .password(account.getPassword())
        .authorities("USER")
        .build()
      )
      .orElseThrow(() -> new UsernameNotFoundException("User name: " + username));
  }

}
