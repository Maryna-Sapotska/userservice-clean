package com.innowise.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            Key signKey = Keys.hmacShaKeyFor(keyBytes);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);

            if (userId != null && role != null) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
