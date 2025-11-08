package com.example.securityaop.config;

import com.example.securityaop.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global Web Configuration
 * ------------------------
 * Enables CORS for frontend integration and registers JWT filter for security.
 */
@Configuration
public class WebConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Enables CORS for all routes — adjust allowed origins for production.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // For local testing; in production use your domain
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

    /**
     * Registers the JWT authentication filter for protected routes.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthFilter);
        registration.addUrlPatterns("/admin/*", "/user/*", "/auth/me");
        registration.setOrder(1);
        return registration;
    }
}
