package com.microservice.authservice.dto;

public record UserResponse(
        String name,
        String email
) {
}
