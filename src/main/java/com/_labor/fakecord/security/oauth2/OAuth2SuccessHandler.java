package com._labor.fakecord.security.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.UserAuthenticatorRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.RefreshTokenService;
import com._labor.fakecord.services.VerificationTokenService;
import com._labor.fakecord.utils.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtCore jwtCore;
  private final RefreshTokenService refreshTokenService;
  private final UserAuthenticatorRepository authenticatorRepository;
  private final VerificationTokenService verificationTokenService;
  
  @Value("${app.frontend-url:http://localhost:5173}")
  private String frontendUrl;
  
  public OAuth2SuccessHandler(JwtCore jwtCore, RefreshTokenService refreshTokenService, UserAuthenticatorRepository authenticatorRepository, @Lazy VerificationTokenService verificationTokenService) {
    this.jwtCore = jwtCore;
    this.refreshTokenService = refreshTokenService;
    this.authenticatorRepository = authenticatorRepository;
    this.verificationTokenService = verificationTokenService;
  }


  @Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    try {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        boolean has2FA = authenticatorRepository.existsByUserIdAndIsActiveTrue(user.getId());

        if (has2FA) {
            String ip = RequestUtil.getClientIp(request);
            String agent = RequestUtil.getClientAgent(request);
            
            var mfaSession = verificationTokenService.createToken(user, TokenType.MFA_SESSION, ip, agent);

            getRedirectStrategy().sendRedirect(request, response, "/?mfaRequired=true&sessionId=" + mfaSession.getId());
            return;
        }

        String accessToken = jwtCore.generateToken(user.getId(), user.getTokenVersion());
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        response.addHeader("Set-Cookie", jwtCore.createAccessTokenCookie(accessToken).toString());
        response.addHeader("Set-Cookie", jwtCore.createRefreshTokenCookie(refreshToken.getToken()).toString());

        clearAuthenticationAttributes(request);
        
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/");
        
    } catch (Exception e) {
        e.printStackTrace(); 
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Auth Success Error: " + e.getMessage());
    }
}
}
