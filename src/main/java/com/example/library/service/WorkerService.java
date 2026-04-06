package com.example.library.service;

import com.example.library.dto.EmployerRegistrationDTO;
import com.example.library.dto.WorkerRegistrationDTO;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
import com.example.library.entity.WorkerProfile;
import com.example.library.repository.JobRepository;
import com.example.library.repository.UserRepository;
import com.example.library.repository.WorkerProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.security.Principal;

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
    private  FileUploadService fileUploadService;
    @Autowired
    private JobRepository jobRepository;


    @Transactional
    public void registerWorker(WorkerRegistrationDTO dto, String healthPath, String idPath) {
        User user = dto.getUser();

        // Κρυπτογράφηση κωδικού
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_WORKER");
        user.setCity(dto.getCity()); // και όχι μόνο στο προφίλ

        // Δημιουργία Προφίλ
        WorkerProfile profile = new WorkerProfile();
        profile.setUser(user);
        profile.setProfession(dto.getProfession());
        profile.setBio(dto.getBio());

        // Αποθήκευση των filenames που ήρθαν από τον Controller
        profile.setHealthCertificatePath(healthPath);
        profile.setIdentificationPath(idPath);
        profile.setProfileVerified(false); // Αναμονή για έγκριση Admin

        // Σύνδεση User -> Profile
        user.setWorkerProfile(profile);

        // Αποθήκευση (Λόγω CascadeType.ALL στον User, θα σωθεί και το Profile)
        userRepository.save(user);
    }

}