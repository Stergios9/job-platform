package com.example.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
public class BookController {

    @GetMapping("/")
    public String index(){
        return "Welcome to Book Controller";
    }
    @GetMapping("/getAllBooks")
    public String getBooks(){
        return "List of books: Spring Boot in Action, Hibernate Pro, etc.";
    }
}
