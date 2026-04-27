package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.controller.common.CustomAuthFailureHandler;
import com.app.controller.common.CustomAuthenticationProvider;
import com.app.controller.common.Routes;
import com.app.security.CustomAuthenticationSuccessHandler;
import com.app.security.PasswordChangeFilter;
import com.app.service.security.CustomUserDetailsService;
import com.app.utils.Constants;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationSuccessHandler successHandler,
            CustomAuthFailureHandler failureHandler, // 👈 AJOUT
            PasswordChangeFilter passwordChangeFilter,
            CustomAuthenticationProvider customAuthenticationProvider // 👈 ICI
    ) throws Exception {

        http
        	
        	.authenticationProvider(customAuthenticationProvider) // 👈 ICI
            // ✅ CSRF activé (obligatoire pour Thymeleaf avec _csrf hidden input)
            // 🔐 Autorisations
            .authorizeHttpRequests(auth -> auth

                // 🔓 Ressources statiques accessibles
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/fonts/**",
                    "/favicon.ico"
                ).permitAll()

                // 🔓 Pages publiques
                .requestMatchers(
                    Routes.ROUTE_LOGIN,                   // /login
                    Routes.ROUTE_ACCESS_DENIED,          // /error/access-denied
                    Routes.ROUTE_LOGOUT,          // ✅ /sign-out
                    "/paiement/**"
                   // "/expired"
                ).permitAll()
                
               // .requestMatchers("/").hasAnyRole("ADMIN", "DIREC")
                // 🔒 Administration uniquement pour ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 🔒 Tout le reste nécessite authentification
                .anyRequest().authenticated()
            )

            // 🔐 FORM LOGIN
            .formLogin(form -> form
                .loginPage(Routes.ROUTE_LOGIN)            // GET /login
                .loginProcessingUrl(Routes.ROUTE_LOGIN)   // POST /login
                .successHandler(successHandler)
               // .failureUrl(Routes.ROUTE_LOGIN + "?error=true")
                .failureHandler(failureHandler) // 👈 ICI
                .permitAll()
            )

            // 🔐 LOGOUT
            .logout(logout -> logout
                .logoutUrl(Routes.ROUTE_LOGOUT)
                .logoutSuccessUrl(Routes.ROUTE_LOGIN + "?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", Constants.COOKIES_NAME)
                .permitAll()
            )
            
            // 🔐 Filtre custom après login
            .addFilterAfter(passwordChangeFilter, UsernamePasswordAuthenticationFilter.class)

            // 🔐 Gestion Forbidden / access denied
            .exceptionHandling(ex -> ex
                .accessDeniedPage(Routes.ROUTE_ACCESS_DENIED)
            )

            // 🔐 Sessions
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(Constants.MAX_SESSION)
                .expiredUrl(Routes.ROUTE_EXPIRED)
            );

        return http.build();
    }
    
}
    
    /*@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return http
            .getSharedObject(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder.class)
            .authenticationProvider(provider)
            .build();
    }*/
    
   /* @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            CustomAuthenticationProvider customAuthenticationProvider) throws Exception {

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider) // 👈 TON PROVIDER
                .build();
    }
}*/
