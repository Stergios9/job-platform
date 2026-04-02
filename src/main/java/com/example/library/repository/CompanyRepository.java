package com.example.library.repository;

import com.example.library.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Η Spring "μεταφράζει" αυτό σε: SELECT COUNT(*) FROM companies WHERE afm = ?
    boolean existsByAfm(String afm);
}
