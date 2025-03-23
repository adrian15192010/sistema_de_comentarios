package com.microservice.comentario.client;

import com.microservice.comentario.dto.AuthenticationTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-authservice", url = "localhost:8050")
public interface AuthClient {

    @GetMapping("/api/auth/is-token-valid/{token}")
    AuthenticationTokenResponse authenticationTokenResponse(@PathVariable String token);

}
