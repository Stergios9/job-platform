package com.example.library.controller;


import com.example.library.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String returnLoginPage(Model model) {

        model.addAttribute("user", new User());
        return "login"; // Returns login.html
    }

    @GetMapping("/")
    public String showLoginPage(Model model) {

        model.addAttribute("user", new User());
        return "login"; // Returns login.html
    }
}