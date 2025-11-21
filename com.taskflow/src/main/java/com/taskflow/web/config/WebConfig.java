package com.taskflow.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 1. Proteger TODO por defecto
                .excludePathPatterns(   // 2. Lista de excepciones (puertas abiertas)
                        "/",              // La página de login
                        "/login",         // Alias del login
                        "/procesar-login",// La acción de enviar el formulario
                        "/style.css",     // Estilos CSS (importante para que se vea bien)
                        "/images/**",     // Imágenes
                        "/error"          // Páginas de error internas
                );
    }
}