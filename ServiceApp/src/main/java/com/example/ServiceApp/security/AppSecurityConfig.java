package com.example.ServiceApp.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    private final AuthenticationProvider appAuthProvider;
    @Bean
    SecurityFilterChain appSecurityFilterChain(HttpSecurity http)throws  Exception{
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests((requests)-> {
            requests.requestMatchers("/register").permitAll();
            requests.requestMatchers("/login").permitAll();
            requests.anyRequest().authenticated();
        });
        http.httpBasic(Customizer.withDefaults());
        http.authenticationProvider(appAuthProvider);
        return http.build();
    }
}
