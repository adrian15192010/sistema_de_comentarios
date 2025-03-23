package com.microservice.comentario.repository;

import com.microservice.comentario.entity.Comentario;
import com.microservice.comentario.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    List<Respuesta> findByComentario(Comentario comentario);

    List<Respuesta> findByComentarioAndToComentario(Comentario comentario, Boolean b);

    List<Respuesta> findByRespuesta(Respuesta respuesta);


}
