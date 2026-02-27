package com._labor.fakecord.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.mappper.SpotifyMapper;
import com._labor.fakecord.infrastructure.integration.spotify.SpotifyClient;
import com._labor.fakecord.infrastructure.integration.spotify.SpotifyProviderStrategy;
import com._labor.fakecord.infrastructure.integration.spotify.SpotifyTokenService;
import com._labor.fakecord.repository.UserConnectionRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com._labor.fakecord.domain.dto.spotify.SpotifyActivity;
import com._labor.fakecord.domain.dto.spotify.SpotifyConnectionState;
import com._labor.fakecord.domain.enums.ConnectionProvider;


@RestController
@RequestMapping("/api/v1/spotify")
@RequiredArgsConstructor
@Slf4j
public class SpotifyController {
  
  private final UserConnectionRepository repository;
  private final SpotifyMapper mapper;
  private final SpotifyClient spotifyClient;
  private final SpotifyTokenService tokenService;
  private final SpotifyProviderStrategy providerStrategy;

  @GetMapping("/connect")
  public ResponseEntity<String> getConnectionUrl(
    @RequestParam UUID userId
  ) {
    String url = providerStrategy.buildAuthorizationUrl(userId);
    return ResponseEntity.ok(url);
  }
  
  @GetMapping("/status")
  public ResponseEntity<SpotifyConnectionState> getStatus(
    @RequestParam UUID userId
  ) {
    return repository.findByUserIdAndProvider(userId, ConnectionProvider.SPOTIFY)
      .map(mapper::toStateDto)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.ok(new SpotifyConnectionState(false, null)));      
  }

  @GetMapping("/activity")
  public ResponseEntity<SpotifyActivity> getActivity(
    @RequestParam UUID userId
  ) {
    return repository.findByUserIdAndProvider(userId, ConnectionProvider.SPOTIFY)
      .flatMap(conn -> {
        String token = tokenService.getValidAccessToken(conn);
        return spotifyClient.getCurrentTrack(token).map(mapper::toActivityDto);
      })
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.noContent().build());
  }

  @DeleteMapping("/disconnect")
  public ResponseEntity<Void> disconnect(
    @RequestParam UUID userId
  ) {
    repository.findByUserIdAndProvider(userId, ConnectionProvider.SPOTIFY)
      .ifPresent(repository::delete);
    log.info("User {} disconnected Spotify account", userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/callback")
  public ResponseEntity handleCallback(
    @RequestParam String code,
    @RequestParam String state,
    @RequestParam UUID userId
  ) {
    log.info("Handling Spotify callback for user: {}", userId);
    
    providerStrategy.handleCallback(userId, code, state);
    
    return ResponseEntity.status(302)
      .header("Location", "http://localhost:3000/settings/connections?provider=spotify&success=true")
      .build();
  }
}
