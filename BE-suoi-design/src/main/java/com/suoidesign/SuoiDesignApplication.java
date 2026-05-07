package com.suoidesign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SuoiDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuoiDesignApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebMvcConfigurer resourceHandlerConfigurer() {
        return new WebMvcConfigurer() {
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
        };
    }
}
