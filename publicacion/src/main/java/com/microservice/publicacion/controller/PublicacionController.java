package com.microservice.publicacion.controller;

import com.microservice.publicacion.client.AuthClient;
import com.microservice.publicacion.client.ComentarioClient;
import com.microservice.publicacion.dto.AuthenticationTokenResponse;
import com.microservice.publicacion.dto.PublicacionDto;
import com.microservice.publicacion.entity.Publicacion;
import com.microservice.publicacion.entity.Reaccion;
import com.microservice.publicacion.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/publicacion")
public class PublicacionController {

 @Autowired
 private AuthClient authClient;

 @Autowired
 private PublicacionRepository publicacionRepository;

 @Autowired
 private ComentarioClient comentarioClient;

 @Autowired
 KafkaTemplate<String, String> kafkaTemplate;


 @PostMapping("/create")
 public ResponseEntity<?> auth(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                final String authentication,
                               @RequestBody PublicacionDto publicacionDto){

     String token = authentication.substring(7);

     AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

     if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

     Publicacion publicacion = publicacionRepository.save(
             Publicacion.builder()
                     .text(publicacionDto.getText())
                     .userId(auth.getUserId().longValue())
                     .build()
     );

     return ResponseEntity.ok(publicacion);
 }

 @DeleteMapping("/delete")
 public ResponseEntity<?> delete(@ModelAttribute("id") Long id,
                                 @RequestHeader(HttpHeaders.AUTHORIZATION)
                                 final String authentication){

     String token = authentication.substring(7);
     AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

     if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

     Publicacion publicacion = publicacionRepository.findById(id).orElseThrow();

     if(publicacion.getUserId().equals(auth.getUserId().longValue())){

         publicacionRepository.delete(publicacion);
         kafkaTemplate.send("Diego-topic", "A "+id);

         return ResponseEntity.ok("Se ha eliminado la publicación");
     }

     throw new RuntimeException();
 }

@GetMapping("/size/pag")
public ResponseEntity<?> findAllPublic(){
    final Pageable pageable = PageRequest.of(0,3);
    return ResponseEntity.ok(publicacionRepository.findAll(pageable).getTotalPages());
}


@GetMapping("/all")
public ResponseEntity<?> findAll( @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                  final String authentication,
                                    @ModelAttribute("pagina") int pagina){

    String token = authentication.substring(7);
    AuthenticationTokenResponse auth = authClient.authenticationTokenResponse(token);

    if (!auth.getIsValid())  return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    final Pageable pageable = PageRequest.of(pagina,3);
     List<Publicacion> publicacionList = publicacionRepository.findAll(pageable).getContent();
     List<PublicacionDto> publicacionDtoList = new ArrayList<>();
    boolean haveYourReaction = false;

     for (Publicacion p : publicacionList){
         haveYourReaction = false;
         for (Reaccion r : p.getReaccionList()){
             if (r.getUserId().equals(auth.getUserId().longValue())){
                 haveYourReaction = true;
                 break;
             }
         }
         publicacionDtoList.add(PublicacionDto.builder()
                 .id(p.getId())
                 .text(p.getText())
                 .userId(p.getUserId())
                 .reaccionList(p.getReaccionList())
                 .haveYourReaction(haveYourReaction)
                 .sizeComentario(comentarioClient.sizeComentario(p.getId()))
                 .build());
     }

     return ResponseEntity.ok(publicacionDtoList);
}

    @GetMapping("/public/all/{pagina}")
    public ResponseEntity<?> paginacion(@PathVariable int pagina){
        final Pageable pageable = PageRequest.of(pagina,3);
        List<Publicacion> publicacionList = publicacionRepository.findAll(pageable).getContent();
        return ResponseEntity.ok(publicacionList);
    }



    @GetMapping("/send")
    public ResponseEntity<?> send(){
        kafkaTemplate.send("Diego-topic", "probando kafka "+(3*3));
        return ResponseEntity.ok("enviado");
    }


}
