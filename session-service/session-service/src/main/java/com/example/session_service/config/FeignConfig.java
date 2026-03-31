package com.example.session_service.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String authHeader = request.getHeader("Authorization");
                String userId = request.getHeader("X-Auth-User-Id");
                String userRole = request.getHeader("X-Auth-User-Role");

                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
                if (userId != null) {
                    requestTemplate.header("X-Auth-User-Id", userId);
                }
                if (userRole != null) {
                    requestTemplate.header("X-Auth-User-Role", userRole);
                }
            }
            
            requestTemplate.header("X-Gateway-Secret", "my-super-secret-gateway-key");
        };
    }
}
