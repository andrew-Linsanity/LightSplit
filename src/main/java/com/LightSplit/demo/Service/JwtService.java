package com.LightSplit.demo.Service;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.LightSplit.demo.Security.SecurityConstants;
import java.security.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

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
}
