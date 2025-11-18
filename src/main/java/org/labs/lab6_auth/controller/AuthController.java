package org.labs.lab6_auth.controller;

import org.labs.lab6_auth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showLoginForm() {
        return "login";
    }

    @PostMapping
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {
        boolean success = userService.login(username, password);

        if (success) {
            model.addAttribute("message", "Successfully logged in!");
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Wrong username or password!");
            return "login";
        }
    }
}

