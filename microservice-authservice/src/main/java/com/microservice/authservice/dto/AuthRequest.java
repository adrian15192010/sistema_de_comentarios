package com.microservice.authservice.dto;

public record AuthRequest(
        String email,
        String password
) {
}
