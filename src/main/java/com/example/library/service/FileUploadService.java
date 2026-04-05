package com.example.library.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    // Η διαδρομή όπου θα αποθηκεύονται οι εικόνες
    private final String UPLOAD_DIR = "uploads/images/";

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // 1. Δημιουργία μοναδικού ονόματος για να μην έχουμε συγκρούσεις
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 2. Δημιουργία του φακέλου αν δεν υπάρχει
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Αποθήκευση του αρχείου στο δίσκο
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName; // Επιστρέφουμε το όνομα για να το σώσουμε στη βάση
    }
}