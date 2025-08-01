//package com.example.ServiceApp.config;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//
////    @Override
////    protected void doFilterInternal(
////            @NonNull HttpServletRequest request,
////            @NonNull HttpServletResponse response,
////            @NonNull FilterChain filterChain)
////            throws ServletException, IOException {
////        final String authHeader = request.getHeader("Authorization");
////        final String jwt;
////        final String userEmail;
////        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
////            filterChain.doFilter(request, response);
////            return;
////        }
////        jwt = authHeader.substring(7);
////        userEmail = jwtService.extractUsername(jwt);
////        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
////            if (jwtService.isTokenValid(jwt, userDetails)) {
////                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
////                        userDetails,
////                        null,
////                        userDetails.getAuthorities()
////                );
////                authToken.setDetails(
////                        new WebAuthenticationDetailsSource().buildDetails(request)
////                );
////                SecurityContextHolder.getContext().setAuthentication(authToken);
////            }
////        }
////        filterChain.doFilter(request, response);
////    }
//@Override
//protected void doFilterInternal(
//        @NonNull HttpServletRequest request,
//        @NonNull HttpServletResponse response,
//        @NonNull FilterChain filterChain)
//        throws ServletException, IOException {
//
//    final String authHeader = request.getHeader("Authorization");
//    final String jwt;
//    final String userEmail;
//    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//        filterChain.doFilter(request, response);
//        return;
//    }
//
//    jwt = authHeader.substring(7);
//    userEmail = jwtService.extractUsername(jwt);
//
//    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//        // 🟡 Extragi rolul direct din token:
//        Claims claims = jwtService.extractAllClaims(jwt);
//        String role = claims.get("role", String.class); // ex: "ADMIN"
//
//        // Creezi autoritățile manual:
//        List<SimpleGrantedAuthority> authorities = List.of(
//                new SimpleGrantedAuthority("ROLE_" + role)
//        );
//
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                userEmail, // poți pune userEmail dacă nu vrei UserDetails
//                null,
//                authorities
//        );
//        authToken.setDetails(
//                new WebAuthenticationDetailsSource().buildDetails(request)
//        );
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//    }
//
//    filterChain.doFilter(request, response);
//}
//}

package com.example.ServiceApp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String path = request.getServletPath();
        if (path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Opțional: poți extrage rolul utilizatorului din token pentru debugging
                String role = jwtService.extractUserRole(jwt);
                System.out.println("User role from JWT: " + role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

