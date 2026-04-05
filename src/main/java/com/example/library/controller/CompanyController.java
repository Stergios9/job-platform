package com.example.library.controller;


import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
//@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

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

    @PostMapping("/register/employer")
    public String handleRegistration(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                                     BindingResult result) {

        // 1. Validation (Έλεγχος αν υπάρχουν λάθη στα πεδία)
        if (result.hasErrors()) {
            return "users/registration-form-employer";
        }

        // 2. Έλεγχος αρχείων
        if (dto.getCertificateFile().isEmpty()) {
            result.rejectValue("certificateFile", "error.file", "Το πιστοποιητικό είναι υποχρεωτικό.");
            return "users/registration-form-employer";
        }

        try {
            User user = dto.getUser();
            Company company = dto.getCompany();
            JobPosition job = dto.getJobPosition();

            // 3. Επεξεργασία User
            user.setRole("ROLE_EMPLOYER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 4. ΑΝΕΒΑΣΜΑ ΑΡΧΕΙΩΝ
            // Ανέβασμα Πιστοποιητικού (PDF)
            String certName = fileUploadService.saveImage(dto.getCertificateFile());
            company.setCertificatePath(certName);

            // Ανέβασμα Εικόνας Επιχείρησης (αν ο χρήστης επέλεξε αρχείο)
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                String imageName = fileUploadService.saveImage(dto.getImageFile());
                job.setImageUrl(imageName); // Εδώ αποθηκεύεται το όνομα του αρχείου στη βάση
            }

            // 5. Συσχετίσεις
            user.setCompany(company);
            company.setUser(user);

            job.setCompany(company);
            company.getJobs().add(job);

            // 6. Αποθήκευση
            companyService.saveCompany(company);

            return "redirect:/login?success";

        } catch (IOException e) {
            result.reject("error.upload", "Σφάλμα κατά την αποθήκευση: " + e.getMessage());
            return "users/registration-form-employer";
        }
    }

}