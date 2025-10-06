package com.loimaxto.springauth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/home")
    public String home(Model model, Principal principal, 
                       @AuthenticationPrincipal OAuth2User oAuth2User) {
        
        if (oAuth2User != null) {
            // OAuth2 user
            String name = oAuth2User.getAttribute("name");
            String email = oAuth2User.getAttribute("email");
            model.addAttribute("username", name != null ? name : email);
            model.addAttribute("email", email);
        } else if (principal != null) {
            // Local user
            model.addAttribute("username", principal.getName());
        }
        
        return "home";
    }
}
