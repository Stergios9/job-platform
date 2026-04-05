package com.example.library.controller;

import com.example.library.dto.WorkerRegistrationDTO;
import com.example.library.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkerController {

    private  WorkerService workerService;

    @PostMapping("/register/worker")
    public String handleWorkerRegistration(@Valid @ModelAttribute("workerDto") WorkerRegistrationDTO dto,
                                           BindingResult result, RedirectAttributes redirectAttributes) {

        // 1. Validation Check
        if (result.hasErrors()) {
            return "users/registration-form-worker";
        }

        // 2. Έλεγχος αν υπάρχουν τα απαραίτητα αρχεία (Health & ID)
        if (dto.getHealthCertificateFile().isEmpty() || dto.getIdentificationFile().isEmpty()) {
            result.reject("error.files", "Πρέπει να ανεβάσετε και τα δύο απαραίτητα έγγραφα.");
            return "users/registration-form-worker";
        }

        try {
            // Κλήση του Service για BCrypt, αποθήκευση αρχείων και DB save
            workerService.registerWorker(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Επιτυχής εγγραφή! Τα έγγραφά σας τελούν υπό έλεγχο.");
            return "redirect:/login";

        } catch (IOException e) {
            result.reject("error.upload", "Σφάλμα κατά την αποθήκευση των εγγράφων: " + e.getMessage());
            return "users/registration-form-worker";
        }
    }

}
