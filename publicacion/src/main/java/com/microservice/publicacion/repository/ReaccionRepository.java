package com.microservice.publicacion.repository;

import com.microservice.publicacion.entity.Publicacion;
import com.microservice.publicacion.entity.Reaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {

    List<Reaccion> findByPublicacion(Publicacion publicacion);

}
