package com.microservice.publicacion.service;

import com.microservice.publicacion.repository.ReaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReaccionService {

    @Autowired
    private ReaccionRepository reaccionRepository;


    public void delete(Long id){
        reaccionRepository.deleteById(id);
    }

}
