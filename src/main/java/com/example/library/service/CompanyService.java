package com.example.library.service;

import com.example.library.entity.Company;
import com.example.library.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

//    public void prepareCompanyRegistration(Company company) {
//        if (company.getRegistrationNumber() == null || company.getRegistrationNumber().isEmpty()) {
//            // Παράγει ένα τυχαίο HE number, π.χ. HE 492103
//            String randomNum = String.valueOf(100000 + new Random().nextInt(900000));
//            company.setRegistrationNumber("HE " + randomNum);
//        }
//    }
//
//    public Company createCompany(Company company) {
//        // Generate number: "HE" + 6 random digits
//        String newRegNumber = generateUniqueRegistrationNumber();
//        company.setRegistrationNumber(newRegNumber);
//
//        return companyRepository.save(company);
//    }

    @Transactional
    public Company saveCompany(Company company) {
        // Ο έλεγχος που ζήτησες

        String newRegNumber = generateUniqueRegistrationNumber();
        company.setRegistrationNumber(newRegNumber);
        if (companyRepository.existsByAfm(company.getAfm())) {
            throw new RuntimeException("Αυτό το ΑΦΜ (" + company.getAfm() + ") είναι ήδη εγγεγραμμένο!");
        }

        return companyRepository.save(company);
    }

    private String generateUniqueRegistrationNumber() {
        Random random = new Random();
        String regNumber;
        boolean exists;

        do {
            // Generates a number between 100000 and 999999
            int number = 100000 + random.nextInt(900000);
            regNumber = "HE " + number;
            // Check database to ensure uniqueness
            exists = companyRepository.existsByRegistrationNumber(regNumber);
        } while (exists);

        return regNumber;
    }



}