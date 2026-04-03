package com.example.library.repository;

import com.example.library.entity.JobPosition;
import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobPosition, Long> {
    List<JobPosition> findAll();

    Optional<JobPosition> findById(Long id);

}