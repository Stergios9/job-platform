package com.example.library.dto;

import com.example.library.entity.User;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public class WorkerRegistrationDTO {

    @Valid
    private User user;

    // Στοιχεία προφίλ εργαζόμενου (π.χ. επάγγελμα, πόλη κλπ)
    private String profession;
    private String city;

    // Τα αρχεία που θα ανέβουν στο σύστημα
    private MultipartFile healthCertificateFile; // Πιστοποιητικό Υγείας
    private MultipartFile identificationFile;      // Ταυτότητα ή ARC

    public WorkerRegistrationDTO() {
        this.user = new User();
    }

    public @Valid User getUser() {
        return user;
    }

    public void setUser(@Valid User user) {
        this.user = user;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public MultipartFile getHealthCertificateFile() {
        return healthCertificateFile;
    }

    public void setHealthCertificateFile(MultipartFile healthCertificateFile) {
        this.healthCertificateFile = healthCertificateFile;
    }

    public MultipartFile getIdentificationFile() {
        return identificationFile;
    }

    public void setIdentificationFile(MultipartFile identificationFile) {
        this.identificationFile = identificationFile;
    }

    // Getters and Setters...
}