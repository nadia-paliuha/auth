package org.labs.lab6_auth.controller;

import org.labs.lab6_auth.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    private final PasswordResetService resetService;

    public PasswordResetController(PasswordResetService resetService) {
        this.resetService = resetService;
    }

    @GetMapping("/forgot-password")
    public String showForgotForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam("email") String email, Model model) {

        String result = resetService.createPasswordResetToken(email);

        if (result.equals("SUCCESS")) {
            model.addAttribute("message", "Check your email for password reset link.");
            return "login";
        } else {
            model.addAttribute("error", result);
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {

        if (!resetService.isTokenValid(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String newPassword,
            Model model
    ) {
        String error = resetService.resetPassword(token, newPassword);

        if (!error.equals("true")) {
            model.addAttribute("error", error);
            model.addAttribute("token", token);
            return "reset-password";
        }

        model.addAttribute("message", "Password successfully updated! You can log in now.");
        return "login";
    }
}
