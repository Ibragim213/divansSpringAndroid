package com.example.reviews.controller;

import com.example.reviews.dto.AuthResponse;
import com.example.reviews.dto.UserLoginRequest;
import com.example.reviews.model.User;
import com.example.reviews.service.UserService;
import com.example.reviews.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController { // Убрали @CrossOrigin - используем глобальную конфигурацию

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Проверяем, существует ли пользователь с таким email
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Пользователь с таким email уже существует");
            }

            // Сохраняем пользователя
            User savedUser = userService.registerUser(user);
            
            // Генерируем токен
            String token = jwtUtil.generateToken(savedUser.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(token));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка регистрации: " + e.getMessage());
        }
    }

    // Авторизация
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        try {
            // Аутентификация пользователя
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );

            // Загружаем пользователя и генерируем токен
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неверный email или пароль");
        }
    }
}