package org.labs.lab6_auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.labs.lab6_auth.entity.User;
import org.labs.lab6_auth.repository.UserRepository;
import org.labs.lab6_auth.service.TOTPService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
public class TwoFactorAuthController {
    private final UserRepository userRepository;
    private final TOTPService totpService;

    public TwoFactorAuthController(UserRepository userRepository, TOTPService totpService) {
        this.userRepository = userRepository;
        this.totpService = totpService;
    }

    @GetMapping("/2fa")
    public String show2faForm(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("2fa_username");
        if (username == null) {
            return "redirect:/login";
        }
        return "2fa";
    }

    @PostMapping("/2fa/verify")
    public String verify2fa(HttpServletRequest request,
                            @RequestParam("code") String code,
                            RedirectAttributes ra) {
        String username = (String) request.getSession().getAttribute("2fa_username");
        if (username == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if(totpService.verifyCode(user, code)) {
            User userEntity = userRepository.findByUsername(username).orElseThrow();
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(userEntity.getUsername())
                    .password(userEntity.getPasswordHash())
                    .authorities(Collections.emptyList())
                    .build();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession().removeAttribute("2fa_username");

            return "redirect:/home";
        }else {
            ra.addFlashAttribute("error", "Invalid code");
            return "redirect:/2fa";
        }
    }
}
