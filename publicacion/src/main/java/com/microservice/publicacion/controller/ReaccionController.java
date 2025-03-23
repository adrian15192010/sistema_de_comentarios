package com.microservice.publicacion.controller;

import com.microservice.publicacion.client.AuthClient;
import com.microservice.publicacion.dto.AuthenticationTokenResponse;
import com.microservice.publicacion.entity.Publicacion;
import com.microservice.publicacion.entity.Reaccion;
import com.microservice.publicacion.entity.ReaccionTipo;
import com.microservice.publicacion.repository.PublicacionRepository;
import com.microservice.publicacion.repository.ReaccionRepository;

import com.microservice.publicacion.service.ReaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reaccion")
public class ReaccionController {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private ReaccionRepository reaccionRepository;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private ReaccionService reaccionService;


    @PostMapping("/create")
    public ResponseEntity<?> create(@ModelAttribute("id_publicacion") Long id,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION)
                                    final String authentication){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

        if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<Publicacion> publicacionOptional = publicacionRepository.findById(id);

        if (!publicacionOptional.isPresent()) return ResponseEntity.notFound().build();

        Publicacion publicacion = publicacionOptional.get();
        List<Reaccion> reaccionList = reaccionRepository.findByPublicacion(publicacion);

        for (Reaccion r : reaccionList){
            if (r.getUserId().equals(auth.getUserId().longValue())){

                reaccionRepository.delete(r);
                return ResponseEntity.ok("ya has reaccionado, se ha eliminado");
            }
        }

        Reaccion reaccion = Reaccion.builder()
                .username(auth.getEmail())
                .userId(auth.getUserId().longValue())
                .tipo(ReaccionTipo.LIKE)
                .publicacion(publicacion)
                .build();

        Reaccion reaccionSave = reaccionRepository.save(reaccion);

        return ResponseEntity.ok(reaccionSave);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Reaccion>> findAll(){
        return ResponseEntity.ok(reaccionRepository.findAll());
    }



}
