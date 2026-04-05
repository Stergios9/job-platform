package com.example.library.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads/workers/";

    public String saveFile(MultipartFile file, String subFolder, String username) throws IOException {
        if (file.isEmpty()) return null;

        // Δημιουργία φακέλου αν δεν υπάρχει
        Path staticPath = Paths.get(uploadDir + subFolder);
        if (!Files.exists(staticPath)) {
            Files.createDirectories(staticPath);
        }

        // Ονοματοδοσία: username_filename (π.χ. marios_health.pdf)
        String fileName = username + "_" + file.getOriginalFilename();
        Path filePath = staticPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString(); // Επιστρέφει το path για τη βάση
    }
}