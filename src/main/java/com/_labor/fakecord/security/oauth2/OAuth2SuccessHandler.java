package com._labor.fakecord.security.oauth2;

import java.io.IOException;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtCore jwtCore;
  private final RefreshTokenService refreshTokenService;

  public OAuth2SuccessHandler(JwtCore jwtCore, RefreshTokenService refreshTokenService) {
    this.jwtCore = jwtCore;
    this.refreshTokenService = refreshTokenService;
  }

  @Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    try {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        if (user == null || user.getId() == null) {
            throw new RuntimeException("User or User ID is null after OAuth success");
        }

        String accessToken = jwtCore.generateToken(user.getId());
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Используем встроенный метод, чтобы избежать ошибок форматирования заголовков
        response.addHeader("Set-Cookie", jwtCore.createAccessTokenCookie(accessToken).toString());
        response.addHeader("Set-Cookie", jwtCore.createRefreshTokenCookie(refreshToken.getToken()).toString());

        clearAuthenticationAttributes(request);
        
        // Редирект на корень сайта
        getRedirectStrategy().sendRedirect(request, response, "/");
        
    } catch (Exception e) {
        // Это поможет тебе увидеть реальную причину в консоли IDE
        e.printStackTrace(); 
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Auth Success Error: " + e.getMessage());
    }
}
}
