package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
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
    public String showChooseRolePage() {
        return "users/choose-role";
    }

    @GetMapping("/register/details")
    public String showRegistrationForm(@RequestParam("role") String role, Model model) {
        if ("ROLE_EMPLOYER".equals(role)) {
            model.addAttribute("registrationDto", new EmployerRegistrationDTO());
            return "users/registration-form-employer";
        }
        User user = new User();
        user.setRole("ROLE_WORKER");
        model.addAttribute("user", user);
        return "users/registration-form";
    }

    @PostMapping("/register/employer")
    public String registerEmployer(@ModelAttribute("registrationDto") EmployerRegistrationDTO dto) {
        User user = dto.getUser();
        Company company = dto.getCompany();
        JobPosition job = dto.getJobPosition();

        // 1. Σύνδεση User <-> Company
        user.setRole("ROLE_EMPLOYER");
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Μην ξεχάσεις το encoding!
        company.setUser(user);
        user.setCompany(company);

        // 2. Σύνδεση Company <-> JobPosition
        job.setCompany(company);
        job.setCity(company.getUser().getCity()); // Ή ό,τι πόλη έβαλε στην αγγελία
        company.getJobs().add(job);

        // 3. Αποθήκευση (Λόγω CascadeType.ALL στο User, θα σωθούν όλα αυτόματα!)
        userRepository.save(user);

        return "redirect:/login";
    }


    @GetMapping("/getAllUsers")
    public List<User> getAll() {
        List<User> users = userService.getAllUsers();
        return users; // Return the actual list, not null!
    }
}