package com.microservice.publicacion.http;

import com.microservice.publicacion.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    @Autowired
    private PublicacionRepository publicacionRepository;



    @GetMapping("/publicacion/is_present/{id}")
    public ResponseEntity<?> isPresentPublication(@PathVariable Long id){
        return ResponseEntity.ok(publicacionRepository.findById(id).isPresent());
    }


}
