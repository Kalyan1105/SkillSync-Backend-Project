package com.example.learning_service.client;

import com.example.learning_service.config.FeignConfig;
import com.example.learning_service.dto.UserInternalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/users/internal/{id}")
    UserInternalDTO getUserById(@PathVariable Long id);

    @GetMapping("/users/internal/email/{email}")
    UserInternalDTO getUserByEmail(@PathVariable String email);
}
