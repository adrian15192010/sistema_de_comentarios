package com.microservice.comentario.dto;

import com.microservice.comentario.entity.Reaccion;
import com.microservice.comentario.entity.Respuesta;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComentarioDto {

    private Long id;

    private String text;

    private Long userId;

    private String username;

    private Long publicacionId;

    private List<Respuesta> respuestaList;

    private List<Reaccion> reaccionList;

}
