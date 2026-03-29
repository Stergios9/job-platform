package com.example.library.controller;

import com.example.library.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class JobController {
    @Autowired
    private JobRepository jobRepository;
}
