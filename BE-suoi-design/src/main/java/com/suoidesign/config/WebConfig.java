package com.suoidesign.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from parent directory
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:../assets/");
        registry.addResourceHandler("/user/**")
                .addResourceLocations("file:../user/");
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("file:../admin/");
        registry.addResourceHandler("/layouts/**")
                .addResourceLocations("file:../layouts/");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
