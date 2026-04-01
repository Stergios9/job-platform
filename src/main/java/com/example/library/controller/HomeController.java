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
    public String homePage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Optional<User> user = userRepository.findByUsername(username);
        // Αναζήτηση στη βάση

        if (!(user.isPresent())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Αδυναμία πρόσβασης!");
            return "redirect:/"; // Επιστροφή στη φόρμα
        }

        // 2. Go back to the DB to get the FULL User object
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        // 3. Now you have access to everything!
        model.addAttribute("user", dbUser.getUsername());
        model.addAttribute("email", dbUser.getEmail()); // Assuming your User entity has getEmail()

        return "home";
    }
}
