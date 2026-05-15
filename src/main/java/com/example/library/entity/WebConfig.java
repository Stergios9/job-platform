package com.example.library.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Διαβάζει τη διαδρομή από το application.properties
    // Αν δεν υπάρχει, χρησιμοποιεί το default "uploads"
    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/stored-images/**", "/uploads/**")
                .addResourceLocations(
                        "file:uploads/images/",
                        "file:" + uploadPath + "/"
                );
    }
}