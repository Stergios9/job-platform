package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    // Μέσα στην κλάση User.java
    @NotBlank(message = "Ο κωδικός είναι υποχρεωτικός")
    @Size(min = 8, message = "Τουλάχιστον 8 χαρακτήρες")
    // Μην βάζεις max=20 εδώ, γιατί το BCrypt βγάζει 60 χαρακτήρες!
    private String password;

    @Column(nullable = false)
    private String role; // Π.χ. "ROLE_EMPLOYER" ή "ROLE_WORKER"

    private String city;

    @Column(nullable = false)
    private String email;

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

    public User(Long id, String username, String password, String role, String city, String email, Company company) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.city = city;
        this.email = email;
        this.company = company;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}