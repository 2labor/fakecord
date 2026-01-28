package com._labor.fakecord.security.oauth2;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.AuthProvider;
import com._labor.fakecord.domain.entity.SocialAccount;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.repository.SocialAccountRepository;
import com._labor.fakecord.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;
  private final SocialAccountRepository socialAccountRepository;
  private final AccountRepository accountRepository;


  public CustomOAuth2UserService(UserRepository userRepository, SocialAccountRepository socialAccountRepository,
      AccountRepository accountRepository) {
    this.userRepository = userRepository;
    this.socialAccountRepository = socialAccountRepository;
    this.accountRepository = accountRepository;
  }

  @Transactional
  @Override
  public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(request);

    String registrationId = request.getClientRegistration().getRegistrationId();
    AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

    String providerId = oAuth2User.getAttribute("sub");
    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");

    if (name == null || name.isEmpty()) {
      name = email != null ? email.split("@")[0] : "User_" + providerId;
    }

    User user = processUserRegistration(provider, providerId, email, name);

    return new CustomOAuth2User(user, oAuth2User.getAttributes());
  }

  private User processUserRegistration(AuthProvider provider, String providerId, String email, String name) {
    Optional<SocialAccount> socialAccountOpt = socialAccountRepository
      .findByProviderAndProviderId(provider, providerId);

    if (socialAccountOpt.isPresent()) {
      return socialAccountOpt.get().getUser();
    }

    User user = accountRepository.findByEmail(email)
      .map(account -> account.getUser())
      .orElseGet(() -> {
        User newUser = new User();
        newUser.setName(name);
        return userRepository.save(newUser);
      });

    if (socialAccountRepository.findByUserAndProvider(user, provider).isEmpty()) {
      SocialAccount socialAccount = new SocialAccount();
      socialAccount.setUser(user);
      socialAccount.setProvider(provider);
      socialAccount.setProviderId(providerId);
      socialAccount.setEmail(email); 
      socialAccountRepository.save(socialAccount);
    }

    return user;
  }
}
