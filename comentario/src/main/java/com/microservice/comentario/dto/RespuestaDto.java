package com.microservice.comentario.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaDto {

    private Long id;

    private String text;

    private Long userId;

    private String username;

    private Long publicacionId;

    private Integer respuestaSize;

}
