package com.example.library.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    // Ορίζουμε που θα αποθηκεύονται τα αρχεία στο Ubuntu
    private final String uploadDir = System.getProperty("user.home") + "/crewnow_uploads/";

    public String saveCertificate(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;

        // Δημιουργία του φακέλου αν δεν υπάρχει
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Δημιουργούμε ένα μοναδικό όνομα αρχείου για να μην υπάρχουν διπλότυπα
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Αποθήκευση του αρχείου στο δίσκο
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // Επιστρέφουμε το όνομα για τη βάση
    }
}
