package com.summer_project.demo.service;

import com.summer_project.demo.dto.UpdateProfileRequest;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User register(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole("USER");
        return (userRepository.save(user));
    }
    public User Login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Invalid password"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
    public User getProfile(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found!"));
    }

    public User updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User Not Found!"));
        user.setUsername(request.getUserName());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }
}
