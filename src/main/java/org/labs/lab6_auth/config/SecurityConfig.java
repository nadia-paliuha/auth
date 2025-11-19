package org.labs.lab6_auth.config;

import org.labs.lab6_auth.handler.CustomAuthFailureHandler;
import org.labs.lab6_auth.handler.CustomAuthSuccessHandler;
import org.labs.lab6_auth.repository.UserRepository;
import org.labs.lab6_auth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthFailureHandler failureHandler;
    private final CustomAuthSuccessHandler successHandler;

    public SecurityConfig(CustomAuthFailureHandler failureHandler, CustomAuthSuccessHandler successHandler) {
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService(UserRepository userRepository,
                                                           PasswordEncoder passwordEncoder) {
        return new CustomOAuth2UserService(userRepository, passwordEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/activate", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/home").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        //for 2FA
                        .successHandler(successHandler)
                        //handler for errors
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        //for userInfo
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        //redirect to 2FA
                        .successHandler(successHandler)

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll());

        return http.build();
    }

    //for hash passwd
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //for Captcha
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


