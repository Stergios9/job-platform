package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import com.example.library.service.FileUploadService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.example.library.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    FileUploadService fileUploadService;

    @GetMapping("/explore")
    public String explore(Model model, Principal principal) {

        User user = principal != null ? userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found")) : null;


        List<JobPosition> allJobs = jobRepository.findAll().stream()
                .filter(job -> job.getCompany() != null &&
                        job.getCompany().getSubscription() != null &&
                        job.getCompany().getSubscription().isActive())
                .toList();

        if (allJobs.isEmpty()) {
            model.addAttribute("errorMessage", "Δεν βρέθηκαν διαθέσιμες θέσεις εργασίας.");
            // Αντί για redirect, μένουμε στη σελίδα αλλά δείχνουμε το μήνυμα
            return "jobs/explore";
        }
        model.addAttribute("allJobs", allJobs);
        return "jobs/explore";
    }

    @GetMapping("/details/{id}")
    public String showJobDetails(@PathVariable Long id, Model model, Principal principal) {

        User user = principal != null ? userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found")) : null;

        String  role = user != null  ? user.getRole() : null;
        JobPosition job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + id));

        model.addAttribute("job", job);
        model.addAttribute("role", role);
        return "jobs/job-details";
    }


    @PostMapping("/update")
    public String updateJob(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                            BindingResult result,
                            Principal principal, RedirectAttributes redirectAttributes,
                            Model model) throws IOException {

        User existingUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ελέγχουμε αν υπάρχουν σφάλματα ΠΟΥ ΔΕΝ ΑΦΟΡΟΥΝ τον κωδικό
        boolean hasOtherErrors = result.getFieldErrors().stream()
                .anyMatch(error -> !error.getField().equals("user.password"));

        if (hasOtherErrors) {
            model.addAttribute("isEdit", true);
            return "users/registration-form-employer";
        }

        // 1. Φορτώνουμε την υπάρχουσα αγγελία από τη βάση για να είμαστε σίγουροι ότι υπάρχει
        JobPosition existingJob = jobRepository.findById(dto.getJobPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + dto.getJobPosition().getId()));

        // SECURITY CHECK
        if (!existingJob.getCompany().getUser().getUsername().equals(principal.getName())) {
            return "redirect:/user/explore/employer?error=unauthorized";
        }

        // ===== UPDATE FIELDS (ONLY IF PROVIDED) =====

        // Title
        if (dto.getJobPosition().getTitle() != null) {
            existingJob.setTitle(dto.getJobPosition().getTitle());
        }

        // Description
        if (dto.getJobPosition().getDescription() != null) {
            existingJob.setDescription(dto.getJobPosition().getDescription());
        }

        // City (if exists in your entity)
        if (dto.getJobPosition().getCity() != null) {
            existingJob.setCity(dto.getJobPosition().getCity());
        }

        // Salary / other fields (example)
        if (dto.getJobPosition().getHourlyRate() != null) {
            existingJob.setHourlyRate(dto.getJobPosition().getHourlyRate());
        }

        MultipartFile imageFile = dto.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {

            fileUploadService.replaceFile(existingJob.getImageUrl(), imageFile, "images");

            String filename = fileUploadService.saveFile(imageFile, "images");
            existingJob.setImageUrl(filename);
        }

        // Λόγω CascadeType.ALL, σώζοντας το Job ή την Company, ενημερώνονται και τα υπόλοιπα
        jobRepository.save(existingJob);
        // Φιλτράρισμα θέσεων εργασίας


        Company company = existingJob.getCompany();
        List<JobPosition> employerJobs = company.getJobs().stream()
                .filter(job -> company.getSubscription() != null &&
                        company.getSubscription().isActive())
                .toList();

        if (employerJobs.isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", "Δεν έχετε δημοσιεύσει θέσεις εργασίας ή η συνδρομή σας δεν είναι ενεργή.");
            // Ανακατεύθυνση στο choose-role με παράμετρο role
            return "redirect:/login?role=employer";
        }
        dto.setCompany(company);
        model.addAttribute("employerJobs", employerJobs);
        model.addAttribute("registrationDto", dto);
        return "jobs/employer-jobs";
    }

    @GetMapping("/edit/{id}")
    public String editJob(@PathVariable("id") Long jobId, Model model, Principal principal) {

        User existingUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Φέρνουμε το Job και προκαλούμε το φόρτωμα της Company (Eager fetch μέσω κώδικα)
        JobPosition job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + jobId));

        // 2. Έλεγχος Ασφαλείας
        String currentUsername = principal.getName();
        Company company = job.getCompany(); // Αυτή είναι η εταιρεία της αγγελίας

        if (!company.getUser().getUsername().equals(currentUsername)) {
            return "redirect:/jobs/explore/employer?error=unauthorized";
        }

        // 3. Δημιουργία και γέμισμα του DTO
        EmployerRegistrationDTO dto = new EmployerRegistrationDTO();

        // ΣΗΜΑΝΤΙΚΟ: Παίρνουμε τον User απευθείας από την εταιρεία της αγγελίας
        User owner = company.getUser();

        dto.setUser(owner);
        dto.setCompany(company);
        dto.setJobPosition(job);

        // Password: Το κρατάμε για να μην χτυπήσει το @NotBlank στο submit
        // (αν και στο update θα το παρακάμψουμε όπως είπαμε πριν)
//        dto.getUser().setPassword(owner.getPassword());

        model.addAttribute("registrationDto", dto);
        model.addAttribute("isEdit", true);
//        return "users/registration-form-employer";
        return "jobs/edit-job";
    }


    @GetMapping("/explore/employer")
    public String showEmployerJobs(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        // 1. Get the username from the security context
        String username = principal.getName();
//        Optional<User> userOpt = userRepository.findByUsername(username);

        // 2. Fetch the User entity (The "User" is now carried by the DB session)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Company company = user.getCompany();

        if (company == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Δεν βρέθηκε εταιρεία συνδεδεμένη με αυτόν τον λογαριασμό.");
            // Ανακατεύθυνση στο choose-role με παράμετρο role
            return "redirect:/login?role=employer";
        }

        // Φιλτράρισμα θέσεων εργασίας
        List<JobPosition> employerJobs = company.getJobs().stream()
                .filter(job -> company.getSubscription() != null &&
                        company.getSubscription().isActive())
                .toList();

        if (employerJobs.isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", "Δεν έχετε δημοσιεύσει θέσεις εργασίας ή η συνδρομή σας δεν είναι ενεργή.");
            // Ανακατεύθυνση στο choose-role με παράμετρο role
            return "redirect:/login?role=employer";
        }

        model.addAttribute("employerJobs", employerJobs);

        return "jobs/employer-jobs";
    }

    @GetMapping("/createNewJob")
    public String createJobForm(Principal principal,
                                Model model) {

        // 1. Έλεγχος αν υπάρχει logged-in user
        if (principal == null) {
            return "redirect:/login";
        }

        // 2. Βρίσκουμε τον user
        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // 3. Έλεγχος αν ο user έχει company
        Company company = user.getCompany();

        if (company == null) {
            model.addAttribute("error",
                    "Δεν βρέθηκε εταιρεία για αυτόν τον χρήστη.");

            return "home";
        }

        // 4. Δημιουργία DTO
        EmployerRegistrationDTO dto =
                new EmployerRegistrationDTO();

        // 5. Μεταφορά user/company
        dto.setUser(user);
        dto.setCompany(company);

        // 6. Δημιουργία νέας αγγελίας
        dto.setJobPosition(new JobPosition());

        // 7. Στέλνουμε το DTO στο form
        model.addAttribute("registrationDto", dto);

        return "jobs/new-job-form";
    }


    @Transactional
    @PostMapping("/saveNewJob")
    public String saveNewJob(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                             BindingResult result,
                             Principal principal, RedirectAttributes redirectAttributes,
                             Model model) throws IOException {


        // Βρίσκουμε την εταιρεία του συνδεδεμένου εργοδότη
        User user = userRepository.findByUsername(principal.getName()).get();
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Δεν βρέθηκε ο χρήστης.");
            return "redirect:/login";
        }
        if (!user.getRole().equals("ROLE_EMPLOYER")) {
            return "redirect:/error?error=unauthorized";
        }
        Company company = user.getCompany();
        if (company == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Δεν βρέθηκε εταιρεία συνδεδεμένη με αυτόν τον λογαριασμό.");
            return "redirect:/login?role=employer";
        }
//        dto.setUser(user);
//        dto.setCompany(company);
//
//        if (result.hasErrors()) {
//            return "jobs/new-job-form";
//        }

        JobPosition job = new JobPosition();
        job.setCompany(company);
        job.setTitle(dto.getJobPosition().getTitle());
        job.setDescription(dto.getJobPosition().getDescription());
        job.setCity(dto.getJobPosition().getCity());
        job.setHourlyRate(dto.getJobPosition().getHourlyRate());


        MultipartFile imageFile = dto.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = fileUploadService.saveFile(imageFile, "images");
            job.setImageUrl(filename);
        }

        jobRepository.save(job);

        redirectAttributes.addFlashAttribute("successMessage", "Η νέα θέση εργασίας δημιουργήθηκε με επιτυχία!");
        return "redirect:/home";
    }


}