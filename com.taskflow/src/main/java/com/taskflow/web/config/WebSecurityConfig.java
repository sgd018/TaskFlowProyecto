package com.taskflow.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    // ESTA es la herramienta que encriptará en la capa MVC
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso a estáticos, login y endpoints públicos
                .requestMatchers("/", "/login", "/procesar-login", "/style.css", "/images/**").permitAll()
                // El resto requiere autenticación (opcional, por ahora lo dejamos abierto para facilitar desarrollo)
                .anyRequest().permitAll() 
            )
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para facilitar las pruebas POST
            .formLogin(form -> form.disable()) // Desactivar el login por defecto de Spring
            .logout(logout -> logout.permitAll());
            
        return http.build();
    }
}