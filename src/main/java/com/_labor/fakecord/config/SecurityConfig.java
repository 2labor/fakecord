package com._labor.fakecord.config;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

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

import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.TokenFilter;
import com._labor.fakecord.security.oauth2.CustomOAuth2UserService;
import com._labor.fakecord.security.oauth2.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserRepository userRepository;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final CustomOAuth2UserService customOAuth2UserService;

  public SecurityConfig(UserRepository userRepository, OAuth2SuccessHandler oAuth2SuccessHandler,
      CustomOAuth2UserService customOAuth2UserService) {
    this.userRepository = userRepository;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    this.customOAuth2UserService = customOAuth2UserService;
  }

  // password encoder
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecureRandom secureRandom() {
    return new SecureRandom();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css").permitAll()
            .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
            .requestMatchers("/mfa-verify", "/api/auth/mfa/verify-totp").permitAll() 
            .requestMatchers("/api/auth/verify", "/api/auth/mfa/backup/verify").permitAll()
            .requestMatchers("/api/auth/**", "/login/**", "/oauth2/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/")
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService))
            .successHandler(oAuth2SuccessHandler));

    http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

    http.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> {
      response.setStatus(401);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Unauthorized: Please log in\"}");
    }));

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository repository) {
    return id -> repository.findById(UUID.fromString(id))
        .map(user -> User
            .withUsername(user.getId().toString())
            .password("")
            .authorities("ROLE_USER")
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("User name: " + id));
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Set-Cookie"));
    configuration.setAllowCredentials(true);

    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
