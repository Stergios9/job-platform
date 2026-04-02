package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // Π.χ. "ROLE_EMPLOYER" ή "ROLE_WORKER"

    private String city;

    /**
     * Αν ο χρήστης είναι Employer, μπορεί να έχει ΜΙΑ εταιρεία.
     * Χρησιμοποιούμε cascade έτσι ώστε αν διαγραφεί ο χρήστης,
     * να διαγραφούν και τα στοιχεία της εταιρείας του.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    // Μέσα στην κλάση User
    public User() {
    }

    // Προσθήκη βοηθητικού constructor
    public User(String username, String password, String role, String city) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}