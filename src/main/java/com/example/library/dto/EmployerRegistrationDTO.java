package com.example.library.dto;

import com.example.library.entity.Company;
import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public class EmployerRegistrationDTO {

    @Valid
    private User user;
    @Valid
    private Company company;
    private JobPosition jobPosition;

    private MultipartFile certificateFile;
    private MultipartFile imageFile;

    public @Valid User getUser() {
        return user;
    }

    public void setUser(@Valid User user) {
        this.user = user;
    }

    public @Valid Company getCompany() {
        return company;
    }

    public void setCompany(@Valid Company company) {
        this.company = company;
    }

    public JobPosition getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(JobPosition jobPosition) {
        this.jobPosition = jobPosition;
    }

    public MultipartFile getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(MultipartFile certificateFile) {
        this.certificateFile = certificateFile;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}