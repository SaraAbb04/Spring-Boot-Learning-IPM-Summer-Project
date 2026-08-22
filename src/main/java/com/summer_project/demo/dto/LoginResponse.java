package com.summer_project.demo.dto;
import com.summer_project.demo.model.Role;
public class LoginResponse {
    private String token;
    private String email;
    private Role role;
    public LoginResponse(String token, String email, Role role){
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
