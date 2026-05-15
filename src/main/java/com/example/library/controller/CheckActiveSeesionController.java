package com.example.library.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CheckActiveSeesionController {

    @GetMapping("/api/check-session")
    public ResponseEntity<?> checkSession() {
//        return ResponseEntity.ok().body(Map.of("active", true));
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) loggedInUser.getPrincipal();
        boolean isAuthenticated = loggedInUser.isAuthenticated() && !"anonymousUser".equals(username);
        if (isAuthenticated) {
            return ResponseEntity.ok().body(Map.of("active", true));
        } else {
            return ResponseEntity.ok().body(Map.of("active", false));
        }
    }
}
