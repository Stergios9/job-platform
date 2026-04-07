package com.example.library.controller;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String homePage(
            @RequestParam(value = "role", required = false) String role,
            Model model,
            Principal principal) {

        String username = principal.getName();
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Αν η παράμετρος role υπάρχει στο URL, την προσθέτουμε στο μοντέλο
        if (role != null) {
            model.addAttribute("selectedRole", role);
        }

        model.addAttribute("username", dbUser.getUsername());
        model.addAttribute("role", dbUser.getRole());

        // Έλεγχος Verification
        boolean isVerified = false;
        if ("ROLE_EMPLOYER".equals(dbUser.getRole()) && dbUser.getCompany() != null) {
            model.addAttribute("isEmployer", true); // Καλύτερα true/false εδώ
            isVerified = dbUser.getCompany().isVerified();
        } else if ("ROLE_WORKER".equals(dbUser.getRole()) && dbUser.getWorkerProfile() != null) {
            model.addAttribute("isWorker", true);
            isVerified = dbUser.getWorkerProfile().isProfileVerified();
        }

        model.addAttribute("isVerified", isVerified);

        return "home";
    }
}
