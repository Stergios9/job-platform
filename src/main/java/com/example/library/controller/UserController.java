package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.repository.UserRepository;
import com.example.library.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.library.entity.User;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @Autowired
    private CompanyService companyService;


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
    public String handleRegistration(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                                     BindingResult result,
                                     Model model) {

        // 1. Έλεγχος για σφάλματα @Size, @Pattern, @NotBlank κλπ από το DTO
        if (result.hasErrors()) {
            // Επιστροφή στο template της εγγραφής (πρόσεξε το όνομα του αρχείου σου, π.χ. "employer-reg")
            return "/users/registration-form";
        }

        try {
            User user = dto.getUser();
            Company company = dto.getCompany();
            JobPosition job = dto.getJobPosition();

            // 2. Προετοιμασία Δεδομένων & Συσχετίσεων
            user.setRole("ROLE_EMPLOYER");

            // Encoding κωδικού (Προϋποθέτει ότι έχεις κάνει @Autowired τον PasswordEncoder)
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Αμφίδρομη σύνδεση User <-> Company
            company.setUser(user);
            user.setCompany(company);

            // Σύνδεση Company <-> JobPosition
            job.setCompany(company);
            company.getJobs().add(job);

            // Αν η πόλη της θέσης είναι κενή, παίρνουμε την πόλη του χρήστη
            if (job.getCity() == null || job.getCity().isEmpty()) {
                job.setCity(user.getCity());
            }

            // 3. Αποθήκευση μέσω του Service (που περιλαμβάνει τον έλεγχο ΑΦΜ)
            // Λόγω CascadeType.ALL στην Company για User και Jobs, σώζονται όλα μαζί.
            companyService.saveCompany(company);

        } catch (RuntimeException e) {
            // 4. Διαχείριση σφάλματος αν το ΑΦΜ υπάρχει ήδη
            // Το "company.afm" αντιστοιχεί στο path του πεδίου μέσα στο DTO σου
            result.rejectValue("company.afm", "error.company", e.getMessage());
            return "/users/registration-form";
        }

        // Επιτυχία! Ανακατεύθυνση με μήνυμα success
        return "redirect:/login?success";
    }


    @GetMapping("/getAllUsers")
    public List<User> getAll() {
        List<User> users = userService.getAllUsers();
        return users; // Return the actual list, not null!
    }
}