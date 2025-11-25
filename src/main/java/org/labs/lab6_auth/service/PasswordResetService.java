package org.labs.lab6_auth.service;
import org.labs.lab6_auth.entity.PasswordResetToken;
import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.repository.PasswordResetTokenRepository;
import org.labs.lab6_auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService,
                                UserService userService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userService = userService;
    }

    public String createPasswordResetToken(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "User with this email was not found.";
        }

        User user = optionalUser.get();

        Optional<PasswordResetToken> lastToken = tokenRepository.findTopByUserOrderByCreatedAtDesc(user);
        if (lastToken.isPresent() && lastToken.get().getCreatedAt().plusMinutes(5).isAfter(LocalDateTime.now())) {
            return "You have recently requested a password reset. Please wait a few minutes before trying again.";
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken reset = new PasswordResetToken();
        reset.setToken(token);
        reset.setUser(user);
        reset.setCreatedAt(LocalDateTime.now());
        reset.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(reset);

        String link = "http://localhost:8080/reset-password?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "Click the link to reset your password: " + link
        );

        return "SUCCESS";
    }


    public String resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            return "Token has expired";
        }

        User user = resetToken.getUser();

        if (!userService.isPasswordValid(newPassword)) {
            return "Password does not meet requirements.";
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return "true";
    }

    public boolean isTokenValid(String token) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = optionalToken.get();
        return !resetToken.isExpired();
    }
}
