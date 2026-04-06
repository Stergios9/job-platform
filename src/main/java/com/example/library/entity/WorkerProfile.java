package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Setter
public class WorkerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isProfileVerified = false;
    private String healthCertificatePath; // PDF
    private String identificationPath;     // Ταυτότητα/ARC

    // Εδώ μπορείς να προσθέσεις και άλλα στοιχεία μόνο για workers
    private String profession; // π.χ. Μπάρμαν
    private String bio;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isProfileVerified() {
        return isProfileVerified;
    }

    public void setProfileVerified(boolean profileVerified) {
        isProfileVerified = profileVerified;
    }

    public String getHealthCertificatePath() {
        return healthCertificatePath;
    }

    public void setHealthCertificatePath(String healthCertificatePath) {
        this.healthCertificatePath = healthCertificatePath;
    }

    public String getIdentificationPath() {
        return identificationPath;
    }

    public void setIdentificationPath(String identificationPath) {
        this.identificationPath = identificationPath;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}