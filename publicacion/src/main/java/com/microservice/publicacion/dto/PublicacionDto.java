package com.microservice.publicacion.dto;

import com.microservice.publicacion.entity.Reaccion;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicacionDto {

    private Long id;

    private String text;

    private Long userId;

    private String username;

    private List<Reaccion> reaccionList;

    private boolean haveYourReaction;

    private Integer sizeComentario;

}
