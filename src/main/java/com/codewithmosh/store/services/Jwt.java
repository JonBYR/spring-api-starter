package com.codewithmosh.store.services;

import com.codewithmosh.store.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@AllArgsConstructor
public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;
    public boolean isValid() {
        return claims.getExpiration().after(new Date()); //check the date it expires is after the current date
    }
    public Long getUserIdFromToken() {

        return Long.valueOf(claims.getSubject()); //subject is the id
    }
    public Role getRoleFromToken() {
        return Role.valueOf(claims.get("role", String.class));
    }
    public String toString() {
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
