package com.example.library.controller;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import com.example.library.service.FileUploadService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.example.library.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
        // Φιλτράρουμε ώστε να βλέπουμε μόνο θέσεις από εταιρείες με ενεργή συνδρομή
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
    public String showJobDetails(@PathVariable Long id, Model model) {
        JobPosition job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + id));

        model.addAttribute("job", job);
        // Πλέον έχουμε πρόσβαση και στα στοιχεία της εταιρείας μέσω του job.getCompany()
        return "jobs/job-details";
    }

    @PostMapping("/update")
    public String updateJob(@Valid @ModelAttribute("registrationDto") EmployerRegistrationDTO dto,
                            BindingResult result,  // <--- ΑΥΤΟ ΕΛΕΙΠΕ
                            Principal principal,
                            Model model) {

        // Τώρα το 'result' αναγνωρίζεται κανονικά
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "users/registration-form-employer";
        }

        // 1. Φορτώνουμε την υπάρχουσα αγγελία από τη βάση για να είμαστε σίγουροι ότι υπάρχει
        JobPosition existingJob = jobRepository.findById(dto.getJobPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + dto.getJobPosition().getId()));

        // 2. Έλεγχος Ασφαλείας (Πολύ σημαντικό!) - Βεβαιωνόμαστε ότι ο χρήστης που κάνει το POST είναι ο ιδιοκτήτης της αγγελίας
        if (!existingJob.getCompany().getUser().getUsername().equals(principal.getName())) {
            return "redirect:/user/explore/employer?error=unauthorized";
        }
        // 3. Ενημέρωση των πεδίων του JobPosition
        existingJob.setTitle(dto.getJobPosition().getTitle());
        existingJob.setHourlyRate(dto.getJobPosition().getHourlyRate());
        existingJob.setDescription(dto.getJobPosition().getDescription());
        existingJob.setCity(dto.getJobPosition().getCity());
        // Αν έχεις και imageUrl, το ενημερώνεις κι αυτό αν άλλαξε
        // existingJob.setImageUrl(dto.getJobPosition().getImageUrl());

        // 4. Ενημέρωση των πεδίων της Εταιρείας (αν επιτρέπεις αλλαγές)
        Company existingCompany = existingJob.getCompany();
        existingCompany.setName(dto.getCompany().getName());
        // Το ΑΦΜ συνήθως δεν το αλλάζουμε, αλλά αν χρειάζεται:
        // existingCompany.setAfm(dto.getCompany().getAfm());

        // 5. Ενημέρωση του User (π.χ. την πόλη του χρήστη)
        User existingUser = existingCompany.getUser();
        existingUser.setCity(dto.getUser().getCity());

        // Χειρισμός νέας εικόνας (αν ανέβηκε)
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            try {
                String fileName = fileUploadService.saveImage(dto.getImageFile());
                existingJob.setImageUrl(fileName);
            } catch (IOException e) {
                result.reject("error.upload", "Αποτυχία ανεβάσματος εικόνας");
                return "users/registration-form-employer";
            }
        }
        // Λόγω CascadeType.ALL, σώζοντας το Job ή την Company, ενημερώνονται και τα υπόλοιπα
        jobRepository.save(existingJob);

        return "redirect:/jobs/explore/employer?success=updated";
    }

    @GetMapping("/explore/employer")
    public String showEmployerJobs(Model model, Principal principal) {
        // 1. Παίρνουμε το username του συνδεδεμένου εργοδότη
        String username = principal.getName();

        // 2. Βρίσκουμε τον χρήστη και την εταιρεία του
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty() || userOpt.get().getCompany() == null) {
            model.addAttribute("errorMessage", "Δεν βρέθηκε εταιρεία συνδεδεμένη με αυτόν τον λογαριασμό.");
            return "jobs/employer-jobs";
        }

        Company company = userOpt.get().getCompany();

        // 3. Φιλτράρουμε τις θέσεις εργασίας της συγκεκριμένης εταιρείας
        // Ελέγχουμε αν η συνδρομή είναι ενεργή
        List<JobPosition> employerJobs = company.getJobs().stream()
                .filter(job -> company.getSubscription() != null &&
                        company.getSubscription().isActive())
                .toList();

        if (employerJobs.isEmpty()) {
            model.addAttribute("errorMessage", "Δεν έχετε δημοσιεύσει θέσεις εργασίας ή η συνδρομή σας δεν είναι ενεργή.");
        }

        model.addAttribute("employerJobs", employerJobs);
        return "jobs/employer-jobs";
    }

    @GetMapping("/edit/{id}")
    public String editJob(@PathVariable("id") Long jobId, Model model, Principal principal) {
        // 1. Βρίσκουμε την αγγελία βάσει ID
        JobPosition job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + jobId));

        // 2. Έλεγχος ασφαλείας: Είναι αυτός ο χρήστης ο ιδιοκτήτης της αγγελίας;
        String username = principal.getName();
        if (!job.getCompany().getUser().getUsername().equals(username)) {
            return "redirect:/user/explore/employer?error=unauthorized";
        }

        // 3. Δημιουργούμε το DTO και το γεμίζουμε με τα υπάρχοντα δεδομένα
        EmployerRegistrationDTO dto = new EmployerRegistrationDTO();
        dto.setUser(job.getCompany().getUser());
        dto.setCompany(job.getCompany());
        dto.setJobPosition(job);

        model.addAttribute("registrationDto", dto);
        model.addAttribute("isEdit", true); // Flag για να ξέρει η φόρμα ότι κάνουμε edit

        return "users/registration-form-employer";
    }
}