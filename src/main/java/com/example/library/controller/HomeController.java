package com.example.library.controller;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        // 1. Το principal δεν θα είναι ΠΟΤΕ null εδώ, γιατί η Security το εγγυάται
        String username = principal.getName();

        // 2. Παίρνουμε τον χρήστη από τη βάση
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Περνάμε τα δεδομένα στο template
        model.addAttribute("username", dbUser.getUsername());
        model.addAttribute("role", dbUser.getRole());

        return "home";
    }
}
