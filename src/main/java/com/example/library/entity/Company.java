package com.example.library.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

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

    @Column(unique = true, length = 9, nullable = false)
    private String afm;

    /**
     * Η σύνδεση με τον User.
     * Εδώ μπαίνει το JoinColumn, άρα ο πίνακας 'companies'
     * θα έχει μια στήλη 'user_id'.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Η σύνδεση με τη Συνδρομή.
     * CascadeType.ALL σημαίνει: αν σώσω την Company, σώζεται αυτόματα και η Subscription.
     */
    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Subscription subscription;

    /**
     * Η σύνδεση με τις θέσεις εργασίας.
     */
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
}
