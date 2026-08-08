package com.summer_project.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;
    public JwtService(@Value("${jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public String generateToken(String email, String role){
        return Jwts.builder().subject(email).claim("role", role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)).signWith(secretKey).compact();
    }
}
