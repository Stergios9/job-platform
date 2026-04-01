package com.example.library.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, length = 9)
    private String afm; // Το ΑΦΜ πρέπει να είναι μοναδικό

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private Subscription subscription;

    @OneToMany(mappedBy = "company")
    private List<JobPosition> jobs;

    // Getters and Setters

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
}