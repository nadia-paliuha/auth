package org.labs.lab6_auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.labs.lab6_auth.exception.EmailAlreadyRegisteredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        if (exception instanceof DisabledException) {
            request.getSession().setAttribute("error", "Your account is not activated. Check your email.");
        } else if (exception instanceof BadCredentialsException) {
            request.getSession().setAttribute("error", "Invalid username or password.");
        }else {
            request.getSession().setAttribute("error", "Authentication failed.");
        }

        response.sendRedirect(request.getContextPath() + "/login");
    }
}
