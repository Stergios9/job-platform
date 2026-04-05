package com.example.library.controller;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        String username = principal.getName();
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("username", dbUser.getUsername());
        model.addAttribute("role", dbUser.getRole());

        // Έλεγχος Verification ανάλογα με τον τύπο
        boolean isVerified = false;
        if ("ROLE_EMPLOYER".equals(dbUser.getRole()) && dbUser.getCompany() != null) {
            model.addAttribute("isEmployer", dbUser.getRole());
            isVerified = dbUser.getCompany().isVerified();
        } else if ("ROLE_WORKER".equals(dbUser.getRole()) && dbUser.getWorkerProfile() != null) {
            model.addAttribute("isWorker", dbUser.getRole());
            isVerified = dbUser.getWorkerProfile().isProfileVerified();
        }

        model.addAttribute("isVerified", isVerified);

        return "home";
    }
}
