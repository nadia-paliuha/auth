package org.labs.lab6_auth.controller;

import org.labs.lab6_auth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping
    public String registerUser(@RequestParam String email,
                               @RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        try {
            userService.register(email, username, password);
            model.addAttribute("message", "Реєстрація успішна! Можете увійти.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
