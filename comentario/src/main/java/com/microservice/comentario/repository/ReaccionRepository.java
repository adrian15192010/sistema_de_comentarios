package com.microservice.comentario.repository;

import com.microservice.comentario.entity.Comentario;
import com.microservice.comentario.entity.Reaccion;
import com.microservice.comentario.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {


    List<Reaccion> findByComentario(Comentario comentario);

    List<Reaccion> findByRespuesta(Respuesta respuesta);

}
