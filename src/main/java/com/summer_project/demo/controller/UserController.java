package com.summer_project.demo.controller;

import com.summer_project.demo.dto.LoginRequest;
import com.summer_project.demo.dto.LoginResponse;
import com.summer_project.demo.model.User;
import com.summer_project.demo.service.JwtService;
import com.summer_project.demo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }
    @PostMapping("/login")
    public LoginResponse Login(@RequestBody LoginRequest request){
        User user = userService.Login(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
}
