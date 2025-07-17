package com.microservice.comentario.controller;

import com.microservice.comentario.client.AuthClient;
import com.microservice.comentario.dto.AuthenticationTokenResponse;
import com.microservice.comentario.dto.ComentarioDto;
import com.microservice.comentario.dto.RespuestaDto;
import com.microservice.comentario.entity.Comentario;
import com.microservice.comentario.entity.Respuesta;
import com.microservice.comentario.repository.ComentarioRepository;
import com.microservice.comentario.repository.RespuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/respuesta")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private ComentarioRepository comentarioRepository;


    @PostMapping("/comentario/create/{id_comentario}")
    public ResponseEntity<?> toComentario(@PathVariable Long id_comentario,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication,
                                          @RequestBody RespuestaDto respuestaDto){


        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

        if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<Comentario> comentarioOptional = comentarioRepository.findById(id_comentario);

        if (!comentarioOptional.isPresent()) throw new RuntimeException();

        Comentario comentario = comentarioOptional.get();

        Respuesta respuesta = respuestaRepository.save(
                Respuesta.builder()
                        .text(respuestaDto.getText())
                        .userId(auth.getUserId().longValue())
                        .username(auth.getEmail())
                        .publicacionId(comentario.getPublicacionId())
                        .toComentario(true)
                        .comentario(comentario)
                        .respuesta(null)
                        .build()
        );


        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/respuesta/create/{id_respuesta}")
    public ResponseEntity<?> toRespuesta(@PathVariable Long id_respuesta,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication,
                                          @RequestBody RespuestaDto respuestaDto){


        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

        if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<Respuesta> respuestaOptional = respuestaRepository.findById(id_respuesta);

        if (!respuestaOptional.isPresent()) throw new RuntimeException();

        Respuesta respuesta = respuestaOptional.get();

            Respuesta respuestaToRespuesta = respuestaRepository.save(
                    Respuesta.builder()
                            .text("@"+respuesta.getUsername()+" "+respuestaDto.getText())
                            .userId(auth.getUserId().longValue())
                            .username(auth.getEmail())
                            .publicacionId(respuesta.getPublicacionId())
                            .toComentario(false)
                            .comentario(respuesta.getComentario())
                            .respuesta(respuesta)
                            .build()
            );

        return ResponseEntity.ok(respuestaToRespuesta);
    }

    @GetMapping("/comentario/respuestas")
    public ResponseEntity<?> comentarioAllRespuestas(@ModelAttribute("id") Long id){

        Comentario comentario = comentarioRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(respuestaRepository.findByComentarioAndToComentario(comentario, true)
                .stream()
                .map(respuesta -> RespuestaDto.builder()
                        .id(respuesta.getId())
                        .text(respuesta.getText())
                        .userId(respuesta.getUserId())
                        .username(respuesta.getUsername())
                        .publicacionId(respuesta.getPublicacionId())
                        .respuestaSize(respuesta.getRespuestaList().size())
                        .build())
                .toList());
    }

    @GetMapping("/respuestas")
    public ResponseEntity<?> respuestaAllRespuestas(@ModelAttribute("id") Long id){

        Respuesta GetRespuesta = respuestaRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(respuestaRepository.findByRespuesta(GetRespuesta)
                .stream()
                .map(respuesta -> RespuestaDto.builder()
                        .id(respuesta.getId())
                        .text(respuesta.getText())
                        .userId(respuesta.getUserId())
                        .username(respuesta.getUsername())
                        .publicacionId(respuesta.getPublicacionId())
                        .respuestaSize(respuesta.getRespuestaList().size())
                        .build())
                .toList());
    }


}
