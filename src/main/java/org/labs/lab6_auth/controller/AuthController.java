package org.labs.lab6_auth.controller;

import jakarta.servlet.http.HttpServletRequest;
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
    public String showLoginForm(HttpServletRequest request, Model model) {
        //show errors
        Object error = request.getSession().getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("error");
        }
        return "login";
    }
}

