package com.LightSplit.demo.Security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.*;

@Component
public class JWTGenerator {

    public String generateToken(Authentication authentication) {

        String username = authentication.getName();
        Date currentDate = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + SecurityConstants.JWT_EXPIRATION);

        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SecurityConstants.JWT_SECRET));

        String token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(currentDate)
            .setExpiration(expireDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
        
        return token;
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
        } catch (ExpiredJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT is expired", ex);
        } catch (MalformedJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT token", ex);
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT claims string is empty", ex);
        }
    }
}
