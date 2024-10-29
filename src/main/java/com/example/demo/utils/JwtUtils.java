package com.example.demo.utils;

import com.example.demo.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final SecretKey JWT_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Secure key
    private static final int JWT_EXPIRATION_MS = 86400000;

    public static String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(JWT_SECRET_KEY) // Use secure key here
                .compact();
    }

    public static boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Log or handle token validation errors here
        }
        return false;
    }

    public static String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(token).getBody().getSubject();
    }
}