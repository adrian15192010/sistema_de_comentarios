package com.microservice.authservice.controller;

import com.microservice.authservice.client.PublicacionClient;
import com.microservice.authservice.dto.AuthRequest;
import com.microservice.authservice.dto.RegisterRequest;
import com.microservice.authservice.dto.TokenResponse;
import com.microservice.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService service;
    private final PublicacionClient publicacionClient;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        final TokenResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest request) {
        final TokenResponse response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public TokenResponse refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication
    ) {
        return service.refreshToken(authentication);
    }

    @GetMapping("/is-token-valid/{token}")
    public ResponseEntity<?> isTokenValid(@PathVariable String token){
           return ResponseEntity.ok(service.authenticationTokenResponse(token));
    }

    @GetMapping("/log-out")
    public ResponseEntity<String> log_out(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication){
           return ResponseEntity.ok(service.log_out(authentication));
    }


}
