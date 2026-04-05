package com.example.library.repository;

import com.example.library.entity.User;
import com.example.library.entity.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {
}
