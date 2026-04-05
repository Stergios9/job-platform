package com.example.library.service;

import com.example.library.dto.WorkerRegistrationDTO;
import com.example.library.entity.User;
import com.example.library.entity.WorkerProfile;
import com.example.library.repository.UserRepository;
import com.example.library.repository.WorkerProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor // Lombok ή φτιάξε Constructor για τα Repositories
public class WorkerService {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  WorkerProfileRepository workerProfileRepository;
    @Autowired
    private  BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private  FileStorageService fileStorageService;

    @Transactional
    public void registerWorker(WorkerRegistrationDTO dto) throws IOException {
        // 1. Ρύθμιση User
        User user = dto.getUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_WORKER");

        // 2. Αποθήκευση Αρχείων
        String healthPath = fileStorageService.saveFile(
                dto.getHealthCertificateFile(), "health", user.getUsername());
        String idPath = fileStorageService.saveFile(
                dto.getIdentificationFile(), "ids", user.getUsername());

        // 3. Δημιουργία και Σύνδεση WorkerProfile
        WorkerProfile profile = new WorkerProfile();
        profile.setUser(user);
        profile.setProfession(dto.getProfession());
        profile.setHealthCertificatePath(healthPath);
        profile.setIdentificationPath(idPath);
        profile.setProfileVerified(false); // Πάντα false στην αρχή

        // 4. Σύνδεση αμφίδρομη (Bi-directional)
        user.setWorkerProfile(profile);

        // 5. Αποθήκευση (Το CascadeType.ALL στον User θα σώσει και το Profile)
        userRepository.save(user);
    }
}