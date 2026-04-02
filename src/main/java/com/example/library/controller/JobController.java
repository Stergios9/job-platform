package com.example.library.controller;

import com.example.library.entity.JobPosition;
import org.springframework.ui.Model;
import com.example.library.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/explore")
    public String explore(Model model, RedirectAttributes redirectAttributes) {
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
}