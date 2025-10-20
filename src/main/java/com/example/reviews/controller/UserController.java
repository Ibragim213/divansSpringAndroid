package com.example.reviews.controller;

import com.example.reviews.dto.UserDTO;
import com.example.reviews.service.UserService;
import com.example.reviews.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController { // Убрали @CrossOrigin

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // Получение всех пользователей
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserProfile(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}