package com.example.demo.user.dto;

public record LoginResponse(
        String token,
        long expiresIn
) {
}
