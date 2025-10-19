package com.ticketing.ticketservice.config;

import com.ticketing.ticketservice.client.AuthServiceClient;
import com.ticketing.ticketservice.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Call auth-service to validate token and return user info
                UserDTO userDTO = authServiceClient.validateToken("Bearer " + token);

                if (userDTO != null) {
                    String username = userDTO.getUsername();
                    String role = userDTO.getRole();

                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated user {} with role {}", username, role);
                }
            } catch (Exception e) {
                log.error("JWT validation failed", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}