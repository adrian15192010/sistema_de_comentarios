package com.microservice.authservice.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
