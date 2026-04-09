package com.example.library.controller;


import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.*;
import com.example.library.repository.JobApplicationRepository;
import com.example.library.repository.JobRepository;
import com.example.library.repository.UserRepository;
import com.example.library.repository.WorkerProfileRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private WorkerProfileRepository workerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @PostMapping("/verify-worker")
    @Transactional
    public String verifyWorker(@RequestParam Long workerId, @RequestParam Long applicationId) {
        // 1. Επικύρωση του προφίλ του εργαζόμενου γενικά
        WorkerProfile worker = workerRepository.findById(workerId).orElseThrow();
        worker.setProfileVerified(true);

        // 2. Ενημέρωση της συγκεκριμένης αίτησης
        JobApplication app = jobApplicationRepository.findById(applicationId).orElseThrow();
        app.setStatus("ACCEPTED");

        workerRepository.save(worker);
        jobApplicationRepository.save(app);

        return "redirect:/company/dashboard?success=verified";
    }

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
                return "users/registration-form-employer";
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

    @GetMapping("/applications/{role}")
    public String viewApplications(@PathVariable("role") String role,
                                   Principal principal, RedirectAttributes redirectAttributes,
                                   Model model) {
        // Βρίσκουμε την εταιρεία του συνδεδεμένου εργοδότη
        User user = userRepository.findByUsername(principal.getName()).get();
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Δεν βρέθηκε ο χρήστης.");
            return "redirect:/login";
        }
        if (!user.getRole().equals("ROLE_EMPLOYER")) {
            return "redirect:/error?error=unauthorized";
        }
        if (user.getCompany() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Δεν βρέθηκε εταιρεία συνδεδεμένη με αυτόν τον λογαριασμό.");
            return "redirect:/login?role=employer";
        }
        Company company = user.getCompany();
        if (company== null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Δεν βρέθηκε εταιρεία συνδεδεμένη με αυτόν τον λογαριασμό.");
            return "redirect:/login?role=employer";
        }
        // Φέρνουμε όλες τις αιτήσεις που αφορούν τις θέσεις αυτής της εταιρείας
        List<JobApplication> applications = jobApplicationRepository.findByJobPosition_Company(company);
        if(applications.isEmpty()){
            redirectAttributes.addFlashAttribute("infoMessage", "Δεν υπάρχουν αιτήσεις για τις θέσεις σας αυτή τη στιγμή.");
            return "redirect:/login?role=employer";
        }

        model.addAttribute("applications", applications);
        return "employers/applications-list";
    }
}