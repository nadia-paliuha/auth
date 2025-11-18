package org.labs.lab6_auth.controller;

import org.springframework.stereotype.Controller;
import org.labs.lab6_auth.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ActivationController {
    private final UserService userService;

    public ActivationController(UserService userService) {
        this.userService = userService;
    }

    //activate user with link from email
    @GetMapping("/activate")
    public String activateUser(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        if (userService.activateUser(token)) {
            redirectAttributes.addFlashAttribute("message", "Account activated.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Wrong token.");
        }
        return "redirect:/login";
    }
}
