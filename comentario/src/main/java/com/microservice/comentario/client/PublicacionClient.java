package com.microservice.comentario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "publicacion", url = "localhost:8040")
public interface PublicacionClient {

    @GetMapping("/api/validation/publicacion/is_present/{id}")
    boolean isPresent(@PathVariable Long id);

}
