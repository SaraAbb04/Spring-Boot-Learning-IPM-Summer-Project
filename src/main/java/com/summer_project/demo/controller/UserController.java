package com.summer_project.demo.controller;

import com.summer_project.demo.dto.*;
import com.summer_project.demo.model.User;
import com.summer_project.demo.service.JwtService;
import com.summer_project.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody User user){
        User savedUser = userService.register(user);
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LoginResponse Login(@RequestBody LoginRequest request){
        User user = userService.Login(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.FOUND)
    public UserResponse profile(Authentication authentication){
        String email = authentication.getName();
        User user = userService.getProfile(email);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request){
        String currentEmail = authentication.getName();
        User user = userService.updateProfile(currentEmail, request);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request){
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok("Password Changed Successfully!");
    }
}
