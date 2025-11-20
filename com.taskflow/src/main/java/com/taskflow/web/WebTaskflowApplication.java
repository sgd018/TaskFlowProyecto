package com.taskflow.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WebTaskflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebTaskflowApplication.class, args);
    }

    // Bean para hacer peticiones a la API
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}