package com.college.Classes;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretString;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes());
    }

    public String generateToken(String username, int userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // משתמש במפתח מהקובץ
                .compact();
    }
}