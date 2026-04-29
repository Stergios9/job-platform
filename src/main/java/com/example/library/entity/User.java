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
    @NotBlank(message = "Ο κωδικός είναι υποχρεωτικός")
    @Size(min = 8, message = "Τουλάχιστον 8 χαρακτήρες")
    private String password;

    @Column(nullable = false)
    private String role; // Π.χ. "ROLE_EMPLOYER" ή "ROLE_WORKER"

    private String nickName;

    private String city;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WorkerProfile workerProfile;

    // Μέσα στην κλάση User
    public User() {
    }

    public User(Long id, String username, String password, String role, String nickName, String city, Company company, WorkerProfile workerProfile) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.nickName = nickName;
        this.city = city;
        this.company = company;
        this.workerProfile = workerProfile;
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

    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public WorkerProfile getWorkerProfile() {
        return workerProfile;
    }

    public void setWorkerProfile(WorkerProfile workerProfile) {
        this.workerProfile = workerProfile;
    }
}
