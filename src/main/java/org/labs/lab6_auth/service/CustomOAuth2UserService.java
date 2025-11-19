package org.labs.lab6_auth.service;

import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.exception.EmailAlreadyRegisteredException;
import org.labs.lab6_auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        //take info from gitHub
        OAuth2User oAuth2User = super.loadUser(request);

        //get data from response
        String username = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        String finalEmail = (email != null) ? email : username + "@github-oauth.local";

        //create user
        userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(finalEmail);
                    user.setPasswordHash(passwordEncoder.encode("OAUTH_USER"));
                    return userRepository.save(user);
                });

        return oAuth2User;
    }
}
