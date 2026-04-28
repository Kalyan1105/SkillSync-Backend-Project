package com.example.learning_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    String authHeader = attributes.getRequest().getHeader("Authorization");
                    String userId = attributes.getRequest().getHeader("X-Auth-User-Id");
                    String userRole = attributes.getRequest().getHeader("X-Auth-User-Role");

                    if (authHeader != null) {
                        template.header("Authorization", authHeader);
                    }
                    if (userId != null) {
                        template.header("X-Auth-User-Id", userId);
                    }
                    if (userRole != null) {
                        template.header("X-Auth-User-Role", userRole);
                    }
                }
                
                template.header("X-Gateway-Secret", "my-super-secret-gateway-key");
            }
        };
    }
}