package com.example.demo.model.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String avatarUrl;
    private String provider;
    private Instant createdAt;
}
