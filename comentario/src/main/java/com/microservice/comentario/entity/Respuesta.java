package com.microservice.comentario.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private Long userId;

    private String username;

    private Long publicacionId;

    private Boolean toComentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comentario")
    @JsonIgnore
    private Comentario comentario;

    @OneToMany(mappedBy = "respuesta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Respuesta> respuestaList;

    @OneToMany(mappedBy = "respuesta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reaccion> reaccionList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_respuesta")
    @JsonIgnore
    private Respuesta respuesta;


}
