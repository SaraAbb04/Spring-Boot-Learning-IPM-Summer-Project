package com.summer_project.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/Hello")
    public String Hello(){
        return "Hello Sara ! Welcome to Spring Boot Java" ;
    }
}
