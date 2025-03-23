package com.microservice.publicacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comentario", url = "localhost:8030")
public interface ComentarioClient {

    @GetMapping("/api/comentario/size/{publicacionId}")
    Integer sizeComentario(@PathVariable Long publicacionId);

}
