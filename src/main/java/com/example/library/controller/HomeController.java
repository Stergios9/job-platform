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

        // 2. Go back to the DB to get the FULL User object
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        // 3. Now you have access to everything!
        model.addAttribute("user", dbUser.getUsername());
        model.addAttribute("email", dbUser.getEmail()); // Assuming your User entity has getEmail()

        return "home";
    }
}
