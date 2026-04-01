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
        List<JobPosition> allJobs = jobRepository.findAll();
        redirectAttributes.addFlashAttribute("errorMessage", "Empty List");
        if (allJobs.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("allJobs", jobRepository.findAll());
        return "jobs/explore";
    }

    // 2. Σελίδα λεπτομερειών (όταν πατάς στην εικόνα/κάρτα)
    @GetMapping("/details/{id}")
    public String showJobDetails(@PathVariable Long id, Model model) {
        JobPosition job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + id));
        model.addAttribute("job", job);
        return "jobs/job-details";
    }
}
