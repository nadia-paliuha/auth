package org.labs.lab6_auth.error;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;

    public CustomAuthSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.isTwoFactorEnabled() && user.getTwoFactorSecret() != null) {
            request.getSession().setAttribute("2fa_username", username);
            SecurityContextHolder.clearContext();

            response.sendRedirect(request.getContextPath() + "/2fa");
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
