package com.example.library.dto;

import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;

public class EmployerRegistrationDTO {
    private User user;
    private Company company;
    private JobPosition jobPosition;

    // Constructors, Getters, Setters
    public EmployerRegistrationDTO() {
        this.user = new User();
        this.company = new Company();
        this.jobPosition = new JobPosition();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public JobPosition getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(JobPosition jobPosition) {
        this.jobPosition = jobPosition;
    }
}