package com.summer_project.demo.dto;

import com.summer_project.demo.model.Role;

public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    public UserResponse(){

    }
    public UserResponse(String id, String username, String email, Role role){
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
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
