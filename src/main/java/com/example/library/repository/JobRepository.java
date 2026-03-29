package com.example.library.repository;

import com.example.library.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobPosition, Long> {
    List<JobPosition> findByCityAndTitleContaining(String city, String title);
}