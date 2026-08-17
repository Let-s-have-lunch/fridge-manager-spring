package com.teamproject.fridgemanagerspring.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private final String secretString = "your_secret_key_here";
    private final SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes());

    public String generateToken(Long userId) {
        return Jwts.builder()
                .claim("id", userId) // Payload에 id 추가
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }
}