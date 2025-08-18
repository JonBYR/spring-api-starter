package com.codewithmosh.store.filters;

import com.codewithmosh.store.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader =  request.getHeader("Authorization");
        if (authHeader == null && !authHeader.startsWith("Bearer ")) {
            //if token is not passed or doesn't start with bearer, the next part of the chain will kick in
            //this means if the target endpoint requires security, a 403 error would occur
            filterChain.doFilter(request, response);
            return;
        }
        var token = authHeader.replace("Bearer ", "");
        var jwt = jwtService.parse(token);
        if(jwt == null || !jwt.isValid()) {
            filterChain.doFilter(request, response);
            return;
            //if invalid spring security will kick in and return a 403 error
        }
        var role = jwt.getRoleFromToken();
        var userId = jwt.getUserIdFromToken();
        //otherwise token is valid
        var authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        //authenticated users already have logged in and so do not need their password passed through
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //adds more information to request such as ip
        SecurityContextHolder.getContext().setAuthentication(authentication); //stores info about current authenticated user
        filterChain.doFilter(request, response); //perform the next part of the endpoint
        return;
    }
}
