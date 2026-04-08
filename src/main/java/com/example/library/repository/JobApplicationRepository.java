package com.example.library.repository;

import com.example.library.entity.Company;
import com.example.library.entity.JobApplication;
import com.example.library.entity.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByWorkerProfileIdAndJobPositionId(Long workerProfileId, Long jobPositionId);

    List<JobApplication> findByJobPosition_Company(Company company);
}
