package com.summer_project.demo.controller;

import com.summer_project.demo.dto.LoginRequest;
import com.summer_project.demo.model.User;
import com.summer_project.demo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);
    }
    @PostMapping("/login")
    public User Login(@RequestBody LoginRequest request){
        return userService.Login(request.getEmail(), request.getPassword());
    }
}
