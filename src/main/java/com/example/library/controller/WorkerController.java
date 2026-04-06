package com.example.library.controller;

import com.example.library.dto.WorkerRegistrationDTO;
import com.example.library.entity.User;
import com.example.library.entity.WorkerProfile;
import com.example.library.repository.UserRepository;
import com.example.library.repository.WorkerProfileRepository;
import com.example.library.service.FileUploadService;
import com.example.library.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/worker")
@RequiredArgsConstructor
public class WorkerController {

    @Autowired
    private  WorkerService workerService;
    @Autowired
    private  FileUploadService fileUploadService;
    @Autowired
    private WorkerProfileRepository workerProfileRepository;
    @Autowired
    private UserRepository userRepository;



    @PostMapping("/register")
    public String handleWorkerRegistration(@Valid @ModelAttribute("workerDto") WorkerRegistrationDTO dto,
                                           BindingResult result,
                                           RedirectAttributes redirectAttributes) {

        // 1. Validation Check (Από τα Annotations στο DTO)
        if (result.hasErrors()) {
            return "users/registration-form-worker";
        }

        // 2. Έλεγχος αν τα αρχεία είναι κενά
        if (dto.getHealthCertificateFile().isEmpty() || dto.getIdentificationFile().isEmpty()) {
            result.reject("error.files", "Πρέπει να ανεβάσετε και τα δύο απαραίτητα έγγραφα (Πιστοποιητικό & Ταυτότητα).");
            return "users/registration-form-worker";
        }

        try {
            // 3. ΑΠΟΘΗΚΕΥΣΗ ΑΡΧΕΙΩΝ ΣΤΟ ΔΙΣΚΟ
            // Χρησιμοποιούμε το νέο Generic Service που φτιάξαμε
            String healthFileName = fileUploadService.saveFile(dto.getHealthCertificateFile(), "worker_docs/health");
            String idFileName = fileUploadService.saveFile(dto.getIdentificationFile(), "worker_docs/ids");

            // 4. ΕΝΗΜΕΡΩΣΗ ΤΩΝ PATHS ΣΤΟ DTO
            // (Υποθέτουμε ότι το WorkerRegistrationDTO έχει πεδία String για τα ονόματα των αρχείων)
            // Αν δεν έχει, τα περνάμε ως παραμέτρους στο service

            workerService.registerWorker(dto, healthFileName, idFileName);

            redirectAttributes.addFlashAttribute("successMessage", "Επιτυχής εγγραφή! Τα έγγραφά σας τελούν υπό έλεγχο.");
            return "redirect:/login?success";

        } catch (IOException e) {
            result.reject("error.upload", "Σφάλμα κατά την αποθήκευση των εγγράφων: " + e.getMessage());
            return "users/registration-form-worker";
        }
    }

    @PostMapping("/worker/update") // Καλό είναι να έχει το /worker/ μπροστά
    public String updateWorker(@Valid @ModelAttribute("workerDto") WorkerRegistrationDTO dto, // Ταίριασμα με το HTML
                               BindingResult result,
                               Principal principal,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "users/registration-form-worker";
        }

        User existingUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Ενημέρωση βασικών στοιχείων User
        // Προσοχή: Ενημερώνουμε την πόλη στον User αν εκεί την αποθηκεύεις
        existingUser.setCity(dto.getCity());
        existingUser.setUsername(dto.getUser().getUsername());

        // 2. Ενημέρωση WorkerProfile
        WorkerProfile profile = existingUser.getWorkerProfile();
        profile.setProfession(dto.getProfession());
        profile.setBio(dto.getBio());

        // 3. Χειρισμός Αρχείων (Εικόνα Προφίλ, Πιστοποιητικό, Ταυτότητα)
        try {
            // Εικόνα Προφίλ
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                profile.setImageUrl(fileUploadService.saveImage(dto.getImageFile()));
            }
            // Πιστοποιητικό Υγείας
            if (dto.getHealthCertificateFile() != null && !dto.getHealthCertificateFile().isEmpty()) {
                profile.setHealthCertificatePath(fileUploadService.saveImage(dto.getHealthCertificateFile()));
            }
            // Ταυτότητα
            if (dto.getIdentificationFile() != null && !dto.getIdentificationFile().isEmpty()) {
                profile.setIdentificationPath(fileUploadService.saveImage(dto.getIdentificationFile()));
            }
        } catch (IOException e) {
            result.reject("upload.error", "Σφάλμα κατά το ανέβασμα των αρχείων");
            model.addAttribute("isEdit", true);
            return "users/registration-form-worker";
        }

        userRepository.save(existingUser);
        return "redirect:/jobs/explore/worker?success=updated";
    }

    @GetMapping("/worker/edit/{id}")
    public String editWorkerProfile(@PathVariable("id") Long workerId, Model model, Principal principal) {
        // 1. Βρίσκουμε το προφίλ βάσει ID
        WorkerProfile profile = workerProfileRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid worker Id:" + workerId));

        // 2. Έλεγχος ασφαλείας
        if (!profile.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/explore?error=unauthorized";
        }

        // 3. Γεμίζουμε το DTO
        WorkerRegistrationDTO dto = new WorkerRegistrationDTO();
        dto.setUser(profile.getUser());
        dto.setProfession(profile.getProfession());
        dto.setBio(profile.getBio());
        dto.setCity(profile.getUser().getCity());

        model.addAttribute("workerDto", dto);
        model.addAttribute("isEdit", true);

        return "users/registration-form-worker"; // Επιστροφή στη σωστή φόρμα
    }

}
