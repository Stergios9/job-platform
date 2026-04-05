package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.dto.WorkerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.repository.UserRepository;
import com.example.library.service.CompanyService;
import com.example.library.service.FileUploadService;
import com.example.library.service.WorkerService;
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

import java.io.IOException;
import java.util.ArrayList;
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
    private WorkerService workerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FileUploadService fileUploadService;


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
            model.addAttribute("isEdit", false);
            return "users/registration-form-employer";
        }

        // ΝΕΑ ΛΟΓΙΚΗ ΓΙΑ WORKER
        if ("ROLE_WORKER".equals(role)) {
            WorkerRegistrationDTO workerDto = new WorkerRegistrationDTO();
            workerDto.getUser().setRole("ROLE_WORKER"); // Προ-συμπλήρωση ρόλου
            model.addAttribute("workerDto", workerDto);
            model.addAttribute("isEdit", false);
            return "users/registration-form-worker"; // Η νέα σου HTML φόρμα
        }

        return "redirect:/user/signUp";
    }



}