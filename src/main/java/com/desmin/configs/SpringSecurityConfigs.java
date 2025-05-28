/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.desmin.filters.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
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
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/login", "/css/**", "/js/**","/baiviets/{id:\\d+}").permitAll()
                       
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/khoas",
                        "/api/lops",
                        "/api/baiviets",
                        "/api/baiviets/{id}",
                        "/api/hdnks",
                        "/api/dieus",
                        "/api/hknhs",
                        "/api/firebase/test",
                        "/api/baiviets/{id}/comments",
                        "/api/baiviets/{id}/likes",
                        "/api/user/profile/{id}"
                ).permitAll()
                .requestMatchers(
                        "/api/auth/**",
                        "/api/login",
                        "/api/users"
                ).permitAll()
                // Role-based endpoints
                .requestMatchers(HttpMethod.POST, "/api/secure/hdnks", "/api/secure/delete/{id}", "/api/secure/create",
                         "/api/secure/diem-danh", "/api/secure/diem-danh-csv/{hoatDongId}","/api/secure/thong-ke").hasAnyRole("TRO_LY_SINH_VIEN", "CVCTSV")
                .requestMatchers(HttpMethod.DELETE, "/api/secure/delete/{id}").hasAnyRole("TRO_LY_SINH_VIEN", "CVCTSV")
                .requestMatchers(HttpMethod.GET, "/api/secure/sinhviens", "/api/secure/export/csvdrl", "/api/secure/export/pdf","/api/secure/thong-ke",
                        "/api/secure/cho-duyet", "/api/secure/duyet/{minhChungId}", "api/secure/duyet/{minhChungId}", "/api/secure/export-csv/{hoatDongId}"
                ).hasAnyRole("TRO_LY_SINH_VIEN", "CVCTSV")
                .requestMatchers(HttpMethod.POST, "/api/secure/dangkys","/api/secure/bao-thieu/{thamGiaId}").hasRole("SINH_VIEN")
                                        .requestMatchers(HttpMethod.POST, "/api/secure/send").hasAnyRole("SINH_VIEN","TRO_LY_SINH_VIEN")
                .requestMatchers(HttpMethod.GET, "/api/export/**").hasAnyRole("CVCTSV", "TRO_LY_SINH_VIEN")
                .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll());

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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    @Bean
public JavaMailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);
    mailSender.setUsername("waykute3vn@gmail.com");
    mailSender.setPassword("athe tnkf ypho swzu");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true"); // Tùy chọn: hiển thị log gửi mail

    return mailSender;
}
}
