package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.repository.UserRepository;
import com.example.library.service.CompanyService;
import com.example.library.service.FileUploadService;
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
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FileUploadService fileUploadService;


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
            model.addAttribute("isEdit", false); // <--- ΠΡΟΣΘΕΣΕ ΑΥΤΟ
            return "users/registration-form-employer";
        }

        User user = new User();
        user.setRole("ROLE_WORKER");
        model.addAttribute("user", user);
        // Αν και η άλλη φόρμα (registration-form) χρησιμοποιεί το isEdit,
        // καλό είναι να το βάλεις κι εδώ.
        model.addAttribute("isEdit", false);

        return "users/registration-form";
    }


    @PostMapping("/register/employer")
    public String handleRegistration(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                                     BindingResult result) {


        // 1. Βασικός έλεγχος Validation (Validation annotations στο DTO)
        if (result.hasErrors()) {
            return "users/registration-form-employer";
        }

        // 2. Έλεγχος αν το αρχείο PDF είναι κενό (Manual check αν δεν έχεις Custom Validator)
        if (dto.getCertificateFile() == null || dto.getCertificateFile().isEmpty()) {
            result.rejectValue("certificateFile", "error.file", "Το πιστοποιητικό σύστασης είναι απαραίτητο.");
            return "users/registration-form-employer";
        }

        try {
            // 3. Προετοιμασία Δεδομένων
            User user = dto.getUser();
            Company company = dto.getCompany();
            JobPosition job = dto.getJobPosition();

            // Ρυθμίσεις Χρήστη
            user.setRole("ROLE_EMPLOYER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Αμφίδρομες Συσχετίσεις (Circular References)
            user.setCompany(company);
            company.setUser(user);

            job.setCompany(company);
            // Προαιρετικό: αν το List<JobPosition> jobs στην Company είναι initialized
            if (company.getJobs() == null) {
                company.setJobs(new ArrayList<>());
            }
            company.getJobs().add(job);

            if (job.getCity() == null || job.getCity().isEmpty()) {
                job.setCity(user.getCity());
            }

            // 4. Διαχείριση Αρχείου PDF
            String fileName = fileUploadService.saveCertificate(dto.getCertificateFile());
            company.setCertificatePath(fileName);
            company.setVerified(false); // Default false για την Κυπριακή αγορά

            // 5. Αποθήκευση στη Βάση (PostgreSQL)
            // Λόγω CascadeType.ALL στην Company, θα σωθούν αυτόματα User και JobPosition
            companyService.saveCompany(company);

            return "redirect:/login?success";

        } catch (IOException e) {
            // Σφάλμα κατά την αποθήκευση του αρχείου στο Ubuntu
            result.reject("error.upload", "Αποτυχία αποθήκευσης αρχείου: " + e.getMessage());
            return "users/registration-form-employer";

        } catch (RuntimeException e) {
            // Σφάλμα από το Service (π.χ. Διπλότυπο ΑΦΜ ή Registration Number)
            result.rejectValue("company.afm", "error.company", e.getMessage());
            return "users/registration-form-employer";
        }
    }

    @GetMapping("/getAllUsers")
    public List<User> getAll() {
        List<User> users = userService.getAllUsers();
        return users; // Return the actual list, not null!
    }
}