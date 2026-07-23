package com.summer_project.demo.service;

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
        return (userRepository.save(user));
    }
}
