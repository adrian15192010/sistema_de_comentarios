package com.microservice.comentario.controller;

import com.microservice.comentario.client.AuthClient;
import com.microservice.comentario.client.PublicacionClient;
import com.microservice.comentario.dto.AuthenticationTokenResponse;
import com.microservice.comentario.dto.ComentarioDto;
import com.microservice.comentario.entity.Comentario;
import com.microservice.comentario.entity.Reaccion;
import com.microservice.comentario.entity.Respuesta;
import com.microservice.comentario.repository.ComentarioRepository;
import com.microservice.comentario.repository.ReaccionRepository;
import com.microservice.comentario.repository.RespuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PublicacionClient publicacionClient;

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private ReaccionRepository reaccionRepository;

    @Autowired
    private AuthClient authClient;


    @PostMapping("/create/{id_publicacion}")
    public ResponseEntity<?> create(@PathVariable Long id_publicacion,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication,
                                    @RequestBody ComentarioDto comentarioDto){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

        if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!publicacionClient.isPresent(id_publicacion)) throw new RuntimeException();

        Comentario comentario = comentarioRepository.save(
                Comentario.builder()
                        .text(comentarioDto.getText())
                        .userId(auth.getUserId().longValue())
                        .username(auth.getEmail())
                        .publicacionId(id_publicacion)
                        .build()
        );

        List<Respuesta> respuestaList = respuestaRepository.findByComentario(comentario);
        List<Reaccion> reaccionList = reaccionRepository.findByComentario(comentario);

        return ResponseEntity.ok(
                ComentarioDto.builder()
                        .id(comentario.getId())
                        .text(comentario.getText())
                        .userId(comentario.getUserId())
                        .username(comentario.getUsername())
                        .publicacionId(comentario.getPublicacionId())
                        .respuestaList(respuestaList)
                        .reaccionList(reaccionList)
                        .build()

        );
    }

    @GetMapping("/all/{publicacionId}")
    public ResponseEntity<?> findAll(@PathVariable Long publicacionId){
        return ResponseEntity.ok(comentarioRepository.findByPublicacionId(publicacionId)
                .stream()
                .map(comentario -> ComentarioDto.builder()
                        .id(comentario.getId())
                        .text(comentario.getText())
                        .userId(comentario.getUserId())
                        .username(comentario.getUsername())
                        .publicacionId(comentario.getPublicacionId())
                        .respuestaList(respuestaRepository.findByComentario(comentario))
                        .reaccionList(comentario.getReaccionList())
                        .build())
                .toList());
    }

    @GetMapping("/allpuro")
    public ResponseEntity<?> allPuro(){
        return ResponseEntity.ok(comentarioRepository.findAll());
    }


    @GetMapping("/size/{publicacionId}")
    public ResponseEntity<?> myAllComentariosSize(@PathVariable Long publicacionId){

        Integer suma = 0;
        List<Comentario> comentarioList = comentarioRepository.findByPublicacionId(publicacionId);

        for (Comentario c : comentarioList){

            suma += c.getRespuestaList().size() + 1;


        }

        return ResponseEntity.ok(suma);
    }



}
