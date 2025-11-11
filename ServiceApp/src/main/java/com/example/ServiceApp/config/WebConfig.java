package com.example.ServiceApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Schimbă "uploads/" cu calea reală dacă este altă locație
        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:C:/Users/iulia/ServiceApp/ServiceAppGurskMedica/uploads/");
                .addResourceLocations("file:uploads/");
    }

}
