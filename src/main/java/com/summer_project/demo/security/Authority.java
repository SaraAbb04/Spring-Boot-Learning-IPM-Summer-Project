package com.summer_project.demo.security;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;


public class Authority implements GrantedAuthority {
    @NotBlank(message = "authority name can't be empty!")
    private String authority;
    public Authority(){
    }
    public Authority(String authority){
        this.authority = authority;
    }
    @Override
    public String getAuthority() {
        return authority;
    }
    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
