package com.example.tremor_test_be.user;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String loginProvider;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .loginProvider(user.getLoginProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}


