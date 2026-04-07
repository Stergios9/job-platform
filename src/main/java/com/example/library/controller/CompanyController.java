package com.example.library.controller;


import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.Subscription;
import com.example.library.entity.User;
import com.example.library.repository.JobRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.CompanyService;
import com.example.library.service.FileUploadService;
import com.example.library.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FileUploadService fileUploadService;

    @Transactional
    @PostMapping("/register/employer")
    public String handleRegistration(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                                     BindingResult result,Model model) {

        if (result.hasErrors()) {
            // ΠΡΕΠΕΙ να ξαναβάλεις το isEdit γιατί η Thymeleaf το ψάχνει!
            model.addAttribute("isEdit", false);
            return "users/registration-form-employer";
        }

        // Έλεγχος υποχρεωτικού αρχείου άδειας
        if (dto.getCertificateFile() == null || dto.getCertificateFile().isEmpty()) {
            result.rejectValue("certificateFile", "error.required", "Το πιστοποιητικό είναι υποχρεωτικό.");
            return "users/registration-form-employer";
        }

        try {

            // 1. ΠΡΩΤΑ ΕΛΕΓΧΟΣ ΑΦΜ (Πριν κάνεις οτιδήποτε άλλο)
            if (companyService.existsByAfm(dto.getCompany().getAfm())) {
                result.rejectValue("company.afm", "error.duplicate", "Το ΑΦΜ είναι ήδη εγγεγραμμένο!");
                return "company/registration-form-employer";
            }
            User user = dto.getUser();
            Company company = dto.getCompany();
            user.setRole("ROLE_EMPLOYER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 4. ΣΥΝΔΕΣΗ (Χειροκίνητα)
            company.setUser(user);
            // Πιστοποιητικά κλπ
            String certFile = fileUploadService.saveFile(dto.getCertificateFile(), "certificates");
            company.setCertificatePath(certFile);
            company.setVerified(true);

            // 3. ΣΥΝΔΡΟΜΗ
            companyService.createSubscription(company);
            user.setCompany(company);

            // 5. ΕΝΑ ΚΑΙ ΜΟΝΑΔΙΚΟ SAVE
            // Εφόσον έχεις CascadeType.ALL στον User, σώζοντας τον User σώζονται ΤΑ ΠΑΝΤΑ
            // (Company, Subscription κλπ) σε ένα transaction.
//            userService.save(user);
            companyService.saveCompany(company);

            // 6. JobPosition (αν δεν έχεις cascade από Company σε Job)
            JobPosition job = dto.getJobPosition();
            job.setCompany(company);
            jobRepository.save(job);
            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            result.reject("error.global", "Σφάλμα: " + e.getMessage());
            return "users/registration-form-employer";
        }
    }
}