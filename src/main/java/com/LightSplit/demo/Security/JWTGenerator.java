package com.LightSplit.demo.Security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.*;

@Component
public class JWTGenerator {

    public String generateToken(Authentication authentication) {

        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        Key key = Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes());

        String token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
        
        return token;
    }

    public String getUsernameFromJWT(String token) { 
        // Convert secret key string to a byte array
        byte[] keyBytes = Base64.getDecoder().decode(SecurityConstants.JWT_SECRET);
        Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
        
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SecurityConstants.JWT_SECRET);
            Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");

            Jwts.parserBuilder().
                setSigningKey(key)
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT is expired or incorrect, ");
        }
    }
}
