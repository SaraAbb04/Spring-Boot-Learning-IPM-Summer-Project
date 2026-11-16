package com.summer_project.demo.service;

import com.summer_project.demo.dto.ChangePasswordRequest;
import com.summer_project.demo.dto.UpdateProfileRequest;
import com.summer_project.demo.dto.UserResponse;
import com.summer_project.demo.exception.EmailAlreadyExistsException;
import com.summer_project.demo.exception.InvalidPasswordException;
import com.summer_project.demo.exception.UserNotFoundException;
import com.summer_project.demo.exception.UnauthorizedException;
import com.summer_project.demo.model.Role;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setEmail(user.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        user.setUsername(user.getUsername().trim());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EmailAlreadyExistsException("Username already exists");
        }
        user.setRole(Role.USER); // Never accept a role supplied by a public registration request.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException exception) {
            throw new EmailAlreadyExistsException("Email or username already exists");
        }
    }

    public User Login(String email, String password) {
        email = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email or password is incorrect"));

        String storedPassword = user.getPassword();
        boolean matches;
        if (storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$"))) {
            matches = passwordEncoder.matches(password, storedPassword);
        } else {
            // One-time migration for accounts created before BCrypt was introduced.
            matches = storedPassword != null && storedPassword.equals(password);
            if (matches) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }
        if (!matches) {
            throw new UnauthorizedException("Email or password is incorrect");
        }
        return user;
    }

    public User getProfile(String email) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = getProfile(currentEmail);
        if (!StringUtils.hasText(request.getUserName()) || !StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("Username and email are required");
        }
        String normalizedEmail = request.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            if (!existing.getId().equals(user.getId())) {
                throw new EmailAlreadyExistsException("Email already exists");
            }
        });
        user.setUsername(request.getUserName().trim());
        user.setEmail(normalizedEmail);
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException exception) {
            throw new EmailAlreadyExistsException("Email or username already exists");
        }
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getProfile(email);
        if (!StringUtils.hasText(request.getCurrentPassword()) || !StringUtils.hasText(request.getNewPassword())) {
            throw new IllegalArgumentException("Current password and new password are required");
        }
        if (!passwordMatches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (request.getNewPassword().length() < 4 || request.getNewPassword().length() > 16) {
            throw new IllegalArgumentException("New password must be between 4 and 16 characters");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) return false;
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        // Upgrade legacy plaintext passwords during password change as well.
        return storedPassword.equals(rawPassword);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
                .toList();
    }

    public void deleteUser(String email) {
        User user = getProfile(email);
        userRepository.delete(user);
    }
}
