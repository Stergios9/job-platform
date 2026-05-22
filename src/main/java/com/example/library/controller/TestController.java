package com.example.library.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class TestController {

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/test-image")
    public String testImage() {

        File file = new File(uploadPath + "apothikarios.png");

        return "EXISTS: " + file.exists()
                + " | PATH: " + file.getAbsolutePath();
    }
}