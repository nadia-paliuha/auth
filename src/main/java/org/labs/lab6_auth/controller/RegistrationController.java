package org.labs.lab6_auth.controller;

import org.labs.lab6_auth.service.CaptchaService;
import org.labs.lab6_auth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final CaptchaService captchaService;

    public RegistrationController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @GetMapping
    public String showRegistrationForm(@ModelAttribute("error") String error,
                                       @ModelAttribute("email") String email,
                                       @ModelAttribute("username") String username,
                                       Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }
        model.addAttribute("email", email);
        model.addAttribute("username", username);

        return "register";
    }

    @PostMapping
    public String registerUser(@RequestParam String email,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(name = "g-recaptcha-response") String gRecaptchaResponse,
                               RedirectAttributes redirectAttributes) {
        if (!captchaService.verifyCaptcha(gRecaptchaResponse)) {
            redirectAttributes.addFlashAttribute("error", "CAPTCHA verification failed. Are you a bot?");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/register";
        }

        try {
            userService.register(email, username, password);
            redirectAttributes.addFlashAttribute("message", "Successfully registered!. Now you can login.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/register";
        }
    }
}
