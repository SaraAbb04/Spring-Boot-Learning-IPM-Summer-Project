package com.summer_project.demo.controller;

import com.summer_project.demo.dto.UserResponse;
import com.summer_project.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }
}
