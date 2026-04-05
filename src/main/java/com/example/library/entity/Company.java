package com.example.library.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, length = 9)
    @NotBlank(message = "Το ΑΦΜ είναι υποχρεωτικό")
    @Size(min = 9, max = 9, message = "Το ΑΦΜ πρέπει να έχει ακριβώς 9 ψηφία")
    @Pattern(regexp = "^[0-9]*$", message = "Το ΑΦΜ πρέπει να περιέχει μόνο αριθμούς")
    private String afm;

    // Μέσα στην κλάση Company.java
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    // Νέα πεδία για την Κύπρο
    @Column(unique = true)
    private String registrationNumber; // π.χ. HE 123456

    private boolean isVerified = false; // Default: False μέχρι να ελεγχθεί από εσένα

    private String certificatePath; // Το όνομα του αρχείου PDF στο δίσκο

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Subscription subscription;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobPosition> jobs = new ArrayList<>();

    // Βοηθητική μέθοδος για να προσθέτεις jobs εύκολα
    public void addJob(JobPosition job) {
        jobs.add(job);
        job.setCompany(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public List<JobPosition> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobPosition> jobs) {
        this.jobs = jobs;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }
}