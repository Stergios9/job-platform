package com.example.library.controller;

import com.example.library.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.library.entity.User;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


   @PostMapping("/")
    public User addUser(@RequestBody User user) {
       user.setPassword(passwordEncoder.encode(user.getPassword()));
       return user;
   }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User newUser,
                               RedirectAttributes redirectAttributes) {

        // Αναζήτηση στη βάση
        Optional<User> foundUser = userRepository.findByUsername(newUser.getUsername());

        // Αν η isPresent() είναι true, ο χρήστης υπάρχει ήδη!
        if (foundUser.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Το όνομα χρήστη χρησιμοποιείται ήδη!");
            return "redirect:/user/signUp"; // Επιστροφή στη φόρμα
        }

        // 2. Αν η isPresent() είναι false, προχωράμε στην αποθήκευση
        // ΠΡΟΣΟΧΗ: Πρέπει να κρυπτογραφήσεις τον κωδικό!
        String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);

        // Σώζουμε στη βάση
        userRepository.save(newUser);

        redirectAttributes.addFlashAttribute("successMessage", "Η εγγραφή ολοκληρώθηκε!");
        return "redirect:/"; // Πίσω στο login
    }

    @GetMapping("/signUp")
    public String showRegistrationForm(Model model) {
        // Δημιουργούμε ένα κενό αντικείμενο User για να το γεμίσει η φόρμα

        model.addAttribute("user", new User());

        return "users/registration-form"; // Επιστρέφει το αρχείο register.html
    }

    @GetMapping("/getAllUsers")
    public List<User> getAll() {
        List<User> users = userService.getAllUsers();
        return users; // Return the actual list, not null!
    }
}
