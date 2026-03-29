package com.example.library.service;

import com.example.library.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);

    long countUsers();

    List<User> getAllUsers();

    Optional<User> findByUsername(String username);

    User save(User user);
}
