/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 *
 * @author admin
 */
@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@ComponentScan(basePackages = {
    "com.desmin.controllers",
    "com.desmin.repositories",
    "com.desmin.services"
})
public class SpringSecurityConfigs {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/bai-viet/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/khoas","api/lops").permitAll()
                .requestMatchers("/api/auth/**", "/api/login", "/api/users").permitAll() // Thêm /api/login và /api/users
                .requestMatchers(HttpMethod.POST, "/api/bai-viet/*/like").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/secure/profile").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/bai-viet/*/comment").authenticated()
                .requestMatchers("/api/ctsv/**").hasRole("CVCTSV")
                .requestMatchers("/api/tro-ly/**").hasRole("TRO_LY_SINH_VIEN")
                .requestMatchers("/api/sinh-vien/**").hasRole("SINH_VIEN")
                .requestMatchers(HttpMethod.GET, "/api/export/**").hasAnyRole("CVCTSV", "TRO_LY_SINH_VIEN")
                .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary
                = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", "dapxu3zfe",
                        "api_key", "949165689244343",
                        "api_secret", "6LlvlznGessksLFpBXDsu34EMFE",
                        "secure", true));
        return cloudinary;
    }

    @Bean
    @Order(0)
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000/"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    
}
