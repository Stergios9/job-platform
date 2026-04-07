package com.example.library.service;

import com.example.library.entity.Company;
import com.example.library.entity.Subscription;
import com.example.library.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public Company saveCompany(Company company) {
        if (company.getRegistrationNumber() == null) {
            String newRegNumber = generateUniqueRegistrationNumber();
            company.setRegistrationNumber(newRegNumber);
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

    public boolean existsByAfm(String afm) {
        return companyRepository.existsByAfm(afm);
    }

    public void createSubscription(Company company){
        Subscription sub = new Subscription();
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusYears(1));
        sub.setActive(true);
        sub.setCompany(company);
        company.setSubscription(sub);

    }
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

}