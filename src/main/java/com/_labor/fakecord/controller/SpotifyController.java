package com._labor.fakecord.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com._labor.fakecord.domain.dto.spotify.SpotifyActivity;
import com._labor.fakecord.domain.dto.spotify.SpotifyConnectionState;
import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.infrastructure.integration.spotify.SpotifyClient;
import com._labor.fakecord.infrastructure.integration.spotify.SpotifyProviderStrategy;
import com._labor.fakecord.infrastructure.integration.spotify.SpotifyTokenService;
import com._labor.fakecord.domain.mappper.SpotifyMapper;
import com._labor.fakecord.repository.UserConnectionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.UUID;

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

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @GetMapping("/connect")
    public ResponseEntity<String> getConnectionUrl(
            @AuthenticationPrincipal UserDetails userDetails) {
        String url = providerStrategy.buildAuthorizationUrl(getUserId(userDetails));
        return ResponseEntity.ok(url);
    }

    @GetMapping("/status")
    public ResponseEntity<SpotifyConnectionState> getStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        return repository
            .findByUserIdAndProvider(getUserId(userDetails), ConnectionProvider.SPOTIFY)
            .map(mapper::toStateDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.ok(new SpotifyConnectionState(false, null)));
    }

    @GetMapping("/activity")
    public ResponseEntity<SpotifyActivity> getActivity(
            @AuthenticationPrincipal UserDetails userDetails) {
        return repository
                .findByUserIdAndProvider(getUserId(userDetails), ConnectionProvider.SPOTIFY)
                .flatMap(conn -> {
                    String token = tokenService.getValidAccessToken(conn);
                    return spotifyClient.getCurrentTrack(token).map(mapper::toActivityDto);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<Void> disconnect(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        repository.findByUserIdAndProvider(userId, ConnectionProvider.SPOTIFY)
                .ifPresent(repository::delete);
        log.info("User {} disconnected Spotify", userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestParam String code,
            @RequestParam String state) {

        log.info("Spotify callback received, state={}", state);

        try {
            providerStrategy.handleCallback(code, state);

            URI redirect = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .queryParam("provider", "spotify")
                .queryParam("success",  "true")
                .build().toUri();

            return ResponseEntity.status(302)
                .location(redirect)
                .build();

        } catch (SecurityException e) {
            log.warn("Spotify callback security error: {}", e.getMessage());
            URI redirect = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .queryParam("provider", "spotify")
                .queryParam("error",    "invalid_state")
                .build().toUri();
            return ResponseEntity.status(302).location(redirect).build();

        } catch (Exception e) {
            log.error("Spotify callback failed", e);
            URI redirect = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .queryParam("provider", "spotify")
                .queryParam("error",    "server_error")
                .build().toUri();
            return ResponseEntity.status(302).location(redirect).build();
        }
    }

    private UUID getUserId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}