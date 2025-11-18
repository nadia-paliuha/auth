package org.labs.lab6_auth.service;

import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.exception.EmailAlreadyRegisteredException;
import org.labs.lab6_auth.exception.InvalidPasswordException;
import org.labs.lab6_auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[^a-zA-Z\\d].*")) return false;
        return true;
    }

    //algorithm for hash, using BCryptPasswordEncoder
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User register(String email, String username, String password){
        if (!isPasswordValid(password)) {
            throw new InvalidPasswordException("The password does not meet the requirements");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(hashPassword(password));

        user.setActivationToken(UUID.randomUUID().toString());
        user = userRepository.save(user);

        emailService.sendActivationEmail(user.getEmail(), user.getActivationToken());
        return user;
    }

    //for user activation
    public boolean activateUser(String token) {
        return userRepository.findByActivationToken(token)
                .map(user -> {
                    user.setActivated(true);
                    user.setActivationToken(null);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
