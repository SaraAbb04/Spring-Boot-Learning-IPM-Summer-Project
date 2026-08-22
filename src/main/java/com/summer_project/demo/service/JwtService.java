package com.summer_project.demo.service;

import com.summer_project.demo.model.Role;
import io.jsonwebtoken.Claims;
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
    public String generateToken(String email, Role role){
        return Jwts.builder().subject(email).claim("role", role.toString()).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)).signWith(secretKey).compact();
    }
    private Claims getClaims(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }
    public String extractRole(String token){
        return getClaims(token).get("role", String.class);
    }
    public boolean isTokenValid(String token){
        try{
            getClaims(token);
            return true;
        }
        catch (Exception exception){
            return false;
        }
    }
}
