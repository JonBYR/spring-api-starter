package com.codewithmosh.store.services;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
     //amount of seconds in one day
    public Jwt generateAccessToken(User u) {

        return getToken(u, jwtConfig.getAccessTokenExpiration());
    }
    public Jwt generateRefreshToken(User u) {
        return getToken(u, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt getToken(User u, long expiration) {
        String idString = u.getId().toString();
        var claims = Jwts.claims().subject(idString)
                    .add("email", u.getEmail())
                    .add("name", u.getName())
                    .add("role", u.getRole())
                    .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * expiration)).build();
        return new Jwt(claims, jwtConfig.getSecretKey());
        /*
        return Jwts.builder().subject(idString)
                .claim("email", u.getEmail())
                .claim("name", u.getName())
                .claim("role", u.getRole()) //claims can be used to store information about a user in the token
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * expiration))
                .signWith(jwtConfig.getSecretKey()).compact();
        //generate a token
         */
    }
    /*
    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);
            //gets properties of the token
            return claims.getExpiration().after(new Date()); //check the date it expires is after the current date
        }
        catch (JwtException e) {
            return false; //if the jwt token is invalid
        }
    }
     */

    private Claims getClaims(String token) {
        var claims = Jwts.parser().verifyWith(jwtConfig.getSecretKey()).build().parseSignedClaims(token).getPayload();
        return claims;
    }
    /*
    public Long getUserIdFromToken(String token) {

        return Long.valueOf(getClaims(token).getSubject()); //subject is the id
    }
    public Role getRoleFromToken(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }
    */
    public Jwt parse(String token) {
        try {
            var claims = getClaims(token);
            return new  Jwt(claims, jwtConfig.getSecretKey());
        }
        catch (JwtException e) {
            return null;
        }
    }
}
