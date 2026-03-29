package com.example.library.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable for now to avoid 403 errors during testing
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/login", "/user/signUp", "/user/register", "/css/**").permitAll()
                        .requestMatchers("/jobs/post").hasRole("EMPLOYER") // Μόνο εργοδότες
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/")               // Your initial entry point
                        .loginProcessingUrl("/login") // The POST target from the HTML form
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/?error=true")   // If fails, go back to "/" with error
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }
}
