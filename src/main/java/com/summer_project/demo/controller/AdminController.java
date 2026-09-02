package com.summer_project.demo.controller;

import com.summer_project.demo.dto.UserResponse;
import com.summer_project.demo.model.User;
import com.summer_project.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email){
        userService.deleteUser(email);
        return ResponseEntity.ok("User deleted successfully!");
    }
}
