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

        User user = dto.getUser();
        Company company = dto.getCompany();
        JobPosition job = dto.getJobPosition();

        try {
            // 1. ΠΡΟΕΤΟΙΜΑΣΙΑ USER
            user.setRole("ROLE_EMPLOYER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // ΣΗΜΑΝΤΙΚΟ: Αποθηκεύουμε τον User και κρατάμε το επιστρεφόμενο instance (savedUser)
            User savedUser = userService.save(user);

            // 2. ΠΡΟΕΤΟΙΜΑΣΙΑ COMPANY
            // Σύνδεση με τον savedUser (που έχει πλέον ID)
            company.setUser(savedUser);

            // Αν ο User έχει mappedBy στην Company, πρέπει να ενημερώσεις και την άλλη πλευρά
            savedUser.setCompany(company);

            // Πιστοποιητικά κλπ
            String certFile = fileUploadService.saveFile(dto.getCertificateFile(), "certificates");
            company.setCertificatePath(certFile);
            company.setVerified(true);

            // 3. ΣΥΝΔΡΟΜΗ
            Subscription sub = new Subscription();
            sub.setStartDate(LocalDate.now());
            sub.setEndDate(LocalDate.now().plusYears(1));
            sub.setActive(true);
            sub.setCompany(company);
            company.setSubscription(sub);

            // 4. ΑΠΟΘΗΚΕΥΣΗ ΕΤΑΙΡΕΙΑΣ
            // Επειδή ο savedUser είναι ήδη "Persistent", το Hibernate δεν θα προσπαθήσει
            // να κάνει ξανά INSERT, αλλά απλώς θα ενημερώσει το foreign key.
            Company savedCompany = companyService.saveCompany(company);

            // 5. ΠΡΟΕΤΟΙΜΑΣΙΑ & ΑΠΟΘΗΚΕΥΣΗ JOB
            job.setCompany(savedCompany);
            jobRepository.save(job);

            return "redirect:/login?success";

        } catch (Exception e) {
            // Καταγραφή του λάθους για να ξέρεις τι φταίει
            e.printStackTrace();
            result.reject("error.global", "Σφάλμα κατά την αποθήκευση: " + e.getMessage());
            return "users/registration-form-employer";
        }
    }

}