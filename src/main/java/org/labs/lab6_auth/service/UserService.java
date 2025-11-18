package org.labs.lab6_auth.service;

import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[^a-zA-Z\\d].*")) return false;
        return true;
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User register(String email, String username, String password) throws Exception {
        if (!isPasswordValid(password)) {
            throw new Exception("Пароль не відповідає політиці безпеки");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Користувач вже існує");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(hashPassword(password));
        return userRepository.save(user);
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        return passwordEncoder.matches(password, userOpt.get().getPasswordHash());
    }
}
