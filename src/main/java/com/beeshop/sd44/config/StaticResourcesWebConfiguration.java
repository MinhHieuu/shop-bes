package com.beeshop.sd44.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {
    @Value("${beeshop.upload-file.base-uri}")
    private String baseUri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**").addResourceLocations(baseUri);
        // Serve uploaded images at /images/<filename> from all subfolders under base-uri
        registry.addResourceHandler("/images/**").addResourceLocations(baseUri);
    }
}
