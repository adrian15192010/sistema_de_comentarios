package com.microservice.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "publicacion", url = "localhost:8040")
public interface PublicacionClient {



}
