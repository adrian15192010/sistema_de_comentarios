package com.microservice.comentario.controller;

import com.microservice.comentario.client.AuthClient;
import com.microservice.comentario.dto.AuthenticationTokenResponse;
import com.microservice.comentario.entity.Comentario;
import com.microservice.comentario.entity.Reaccion;
import com.microservice.comentario.entity.ReaccionTipo;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/reaccion")
public class ReaccionController {

    @Autowired
    private AuthClient authClient;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ReaccionRepository reaccionRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;


    @PostMapping("/create")
    public ResponseEntity<?> create(@ModelAttribute("id_comentario") Long id,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION)
                                    final String authentication){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

        if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<Comentario> comentarioOptional = comentarioRepository.findById(id);

        if (!comentarioOptional.isPresent()) return ResponseEntity.notFound().build();

        Comentario comentario = comentarioOptional.get();
        List<Reaccion> reaccionList = reaccionRepository.findByComentario(comentario);

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
                .comentario(comentario)
                .respuesta(null)
                .build();

        Reaccion reaccionSave = reaccionRepository.save(reaccion);

        return ResponseEntity.ok(reaccionSave);
    }


    @PostMapping("/create/toRespuesta")
    public ResponseEntity<?> createToRespuesta(@ModelAttribute("id_respuesta") Long id,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION)
                                    final String authentication){

        String token = authentication.substring(7);
        AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

        if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Optional<Respuesta> respuestaOptional = respuestaRepository.findById(id);

        if (!respuestaOptional.isPresent()) return ResponseEntity.notFound().build();

        Respuesta respuesta = respuestaOptional.get();
        List<Reaccion> reaccionList = reaccionRepository.findByRespuesta(respuesta);

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
                .comentario(null)
                .respuesta(respuesta)
                .build();

        Reaccion reaccionSave = reaccionRepository.save(reaccion);

        return ResponseEntity.ok(reaccionSave);
    }



    @GetMapping("/all")
    public ResponseEntity<List<Reaccion>> findAll(){
        return ResponseEntity.ok(reaccionRepository.findAll());
    }



}
