package com.summer_project.demo.service;

import com.summer_project.demo.dto.ChangePasswordRequest;
import com.summer_project.demo.dto.UpdateProfileRequest;
import com.summer_project.demo.dto.UserResponse;
import com.summer_project.demo.exception.EmailAlreadyExistsException;
import com.summer_project.demo.exception.InvalidPasswordException;
import com.summer_project.demo.exception.UserNotFoundException;
import com.summer_project.demo.model.User;
import com.summer_project.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User register(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email already exits!");
        }
        user.setRole("USER");
        return (userRepository.save(user));
    }


    public User Login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User with this email or password not found!" ));
        if(!password.equals(user.getPassword())){
            throw new InvalidPasswordException("Password is incorrect");
        }
        return user;
    }
    public User getProfile(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
    }

    public User updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setUsername(request.getUserName());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }
    public void changePassword(String email, ChangePasswordRequest request){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(!request.getCurrentPassword().equals(user.getPassword())){
            throw new InvalidPasswordException("Current password is incorrect");
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream().map(user -> new UserResponse(user.getId(),user.getUsername(), user.getEmail(), user.getRole())).toList();
    }
}
