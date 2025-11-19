package org.labs.lab6_auth.controller;

import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.repository.UserRepository;
import org.labs.lab6_auth.service.TOTPService;
import org.labs.lab6_auth.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TOTPService totpService;

    public HomeController(UserService userService, TOTPService totpService, UserRepository userRepository) {
        this.userService = userService;
        this.totpService = totpService;
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("twoFactorEnabled", user.isTwoFactorEnabled());

        //for showing qr
        if (!user.isTwoFactorEnabled() && user.getTwoFactorSecret() != null) {
            String qrUrl = userService.generateQrUrlForUser(user);
            model.addAttribute("showQr", qrUrl != null);
            model.addAttribute("qrUrl", qrUrl);
        } else {
            model.addAttribute("showQr", false);
        }

        return "home";
    }

    @PostMapping("/home/enable-2fa")
    public String enable2FA(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        totpService.prepareSecretForUser(user);
        return "redirect:/home";
    }

    @PostMapping("/home/disable-2fa")
    public String disable2FA(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        totpService.disableTwoFactor(user);
        return "redirect:/home";
    }

    @PostMapping("/home/confirm-2fa")
    public String confirm2FA(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("code") String code, RedirectAttributes ra) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        boolean confirmed = totpService.verifyCode(user, code);
        if (confirmed) {
            totpService.enableTwoFactor(user);
            ra.addFlashAttribute("message", "2FA successfully enabled!");
        } else {
            ra.addFlashAttribute("error", "Invalid code. Try again.");
        }
        return "redirect:/home";
    }
}
