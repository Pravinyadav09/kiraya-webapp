package com.app.controller;

import com.app.model.User;
import com.app.repository.UserRepository;

import com.app.service.UserService;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class UserController {

    @Autowired
    private UserService userService;
    @Autowired // Inject UserRepository
    private UserRepository userRepository;


    @PostMapping("/signup")
    public String signUp(@RequestBody User user) {
        return userService.signUp(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.login(user);
    }
    @PostMapping("/forget-password")
    public String forgetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return "User found"; // Just return a success message
        } else {
            return "User not found";
        }
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Update password
            user.setPassword(newPassword); // You should hash the password before saving

            // Save the user
            userRepository.save(user);

            return "Password reset successful";
        } else {
            return "User not found";
        }
    }
    
}
    
