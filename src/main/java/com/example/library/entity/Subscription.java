package com.example.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;
}