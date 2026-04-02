package com.example.library.service;

import com.example.library.entity.Company;
import com.example.library.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Ελέγχει αν το ΑΦΜ υπάρχει ήδη και σώζει την εταιρεία.
     * Χρησιμοποιούμε @Transactional για να είμαστε σίγουροι ότι
     * η εγγραφή θα γίνει σωστά στη βάση.
     */
    @Transactional
    public Company saveCompany(Company company) {
        // Ο έλεγχος που ζήτησες
        if (companyRepository.existsByAfm(company.getAfm())) {
            throw new RuntimeException("Αυτό το ΑΦΜ (" + company.getAfm() + ") είναι ήδη εγγεγραμμένο!");
        }

        return companyRepository.save(company);
    }
}