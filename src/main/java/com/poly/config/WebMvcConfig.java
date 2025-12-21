package com.poly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.poly.interceptor.AdminInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;

    @Value("${upload.path:src/main/resources/static/uploads/avatars}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/** to file system
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .addResourceLocations("classpath:/static/uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký AdminInterceptor cho các routes /admin/**
        // Loại trừ /admin/login và /admin/api/** để cho phép truy cập
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/api/**"
                );
    }
}