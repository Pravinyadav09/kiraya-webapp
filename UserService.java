package com.app.service;

import com.app.model.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

  

    public String signUp(User user) {
        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "User already exists";
        }

        // Save new user
        userRepository.save(user);
        return "Signup successful";
    }

    public String login(User user) {
        // Fetch user from database by email
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        // Check if user exists and password matches
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {
            return "Login successful";
        } else {
            return "Invalid email or password";
        }
    }

    public String forgetPassword(String email) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Generate a reset token
            String resetToken = UUID.randomUUID().toString();
            user.setResetPasswordToken(resetToken);
            user.setResetPasswordExpires(new Date(System.currentTimeMillis() + 3600)); // 1 hour

            // Save the user with the reset token
            userRepository.save(user);

            // Send email with reset link (dummy implementation)
            String resetLink = "http://localhost:8080/api/reset-password?token=" + resetToken;
            System.out.println("Reset link: " + resetLink);

            return "Password reset link sent to your email";
        } else {
            return "User not found";
        }
    }

    public String resetPassword(String token, String newPassword) {
        // Find user by reset token
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if token is expired
            if (user.getResetPasswordExpires().before(new Date())) {
                return "Token has expired";
            }

            // Update password and clear reset token
            user.setPassword(newPassword);
            user.setResetPasswordToken(null);
            user.setResetPasswordExpires(null);

            // Save the user
            userRepository.save(user);

            return "Password reset successful";
        } else {
            return "Invalid token";
        }
    }
}