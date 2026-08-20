package com.summer_project.demo.service;

import com.summer_project.demo.dto.ChangePasswordRequest;
import com.summer_project.demo.dto.UpdateProfileRequest;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User register(User user){
        user.setRole("USER");
        return (userRepository.save(user));
    }


    public User Login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Invalid email or password" ));
        if(!password.equals(user.getPassword())){
            throw new RuntimeException("Password is incorrect");
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
    public void changePassword(String email, ChangePasswordRequest request){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        if(!request.getCurrentPassword().equals(user.getPassword())){
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
