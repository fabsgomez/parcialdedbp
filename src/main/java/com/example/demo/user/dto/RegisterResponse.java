package com.example.demo.user.dto;

public record RegisterResponse(
        Long id,
        String username,
        String email
) {
}
