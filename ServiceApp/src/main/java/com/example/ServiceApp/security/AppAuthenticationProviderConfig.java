package com.example.ServiceApp.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@Configuration
public class AppAuthenticationProviderConfig {
    private final PasswordEncoder appPasswordEncoder;
    private final AppUserDetailsService appUserDetailsService;

    @Bean
    public AuthenticationProvider appAuthenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(appPasswordEncoder);
        authProvider.setUserDetailsService(appUserDetailsService);
        return  authProvider;
    }
}
