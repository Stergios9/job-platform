package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.dto.WorkerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.WorkerProfile;
import com.example.library.repository.UserRepository;
import com.example.library.service.CompanyService;
import com.example.library.service.FileUploadService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.library.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CompanyService companyService;

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


    @GetMapping("/register/details/{role}")
    public String showRegistrationForm(@PathVariable("role") String role, Model model) {
        if ("ROLE_EMPLOYER".equals(role)) {

            model.addAttribute("registrationDto", new EmployerRegistrationDTO());
            model.addAttribute("isEdit", false);
            return "users/registration-form-employer";
        }
        if ("ROLE_WORKER".equals(role)) {
            model.addAttribute("workerDto", new WorkerRegistrationDTO());
            model.addAttribute("isEdit", false);
            return "users/registration-form-worker"; // Η νέα σου HTML φόρμα
        }
        return "redirect:/user/signUp";
    }


    @GetMapping("/profile")
    public String editProfile(Model model, Principal principal) {

        User savedUser = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

         // WORKER
        if ("ROLE_WORKER".equals(savedUser.getRole())) {

            WorkerRegistrationDTO workerDto = new WorkerRegistrationDTO();
            workerDto.setUser(savedUser);
            WorkerProfile profile = savedUser.getWorkerProfile();
            workerDto.setCity(savedUser.getCity());

            if (profile != null) {

                workerDto.setProfession(profile.getProfession());
                workerDto.setBio(profile.getBio());
            }

            model.addAttribute("workerDto", workerDto);
            model.addAttribute("isEdit", true);

            return "users/profile-edit-worker";
        }

        // EMPLOYER
        EmployerRegistrationDTO dto = new EmployerRegistrationDTO();
        dto.setUser(savedUser);
        dto.setCompany(savedUser.getCompany());

        model.addAttribute("registrationDto", dto);
        model.addAttribute("isEdit", true);

        return "users/profile-edit-employer";
    }


    @PostMapping("/company/update")
    @Transactional
    public String updateUserAndCompany(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                            BindingResult result,  // <--- ΑΥΤΟ ΕΛΕΙΠΕ
                            Principal principal,
                            Model model) throws IOException {

        // Ελέγχουμε αν υπάρχουν σφάλματα ΠΟΥ ΔΕΝ ΑΦΟΡΟΥΝ τον κωδικό
        boolean hasOtherErrors = result.getFieldErrors().stream()
                .anyMatch(error -> !error.getField().equals("user.password"));

        if (hasOtherErrors) {
            model.addAttribute("isEdit", true);
            return "users/profile-edit-employer";
        }

        User existingUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Ενημέρωση βασικών στοιχείων User
        existingUser.setCity(dto.getUser().getCity());
        existingUser.setNickName(dto.getUser().getNickName());
        existingUser.setUsername(dto.getUser().getUsername());
//        boss1.setPassword(passwordEncoder.encode("12345678")); // Χειροκίνητο encode
        existingUser.setPassword(passwordEncoder.encode(dto.getUser().getPassword()));

//        userRepository.save(existingUser);

        Company company = existingUser.getCompany();
        existingUser.setCompany(company);
        company.setUser(existingUser);

        company.setVerified(true);
//        companyService.createSubscription(company);

        MultipartFile certificateFile = dto.getCertificateFile();

        if (certificateFile != null && !certificateFile.isEmpty()) {

            fileUploadService.replaceFile(company.getCertificatePath(), certificateFile, "certificates");

            String filename = fileUploadService.saveFile(certificateFile, "certificates");
            company.setCertificatePath(filename);
        }

        companyService.saveCompany(company);

        return "redirect:/jobs/explore/employer?success=updated";
    }

}