package com.microservice.comentario.config;


import com.microservice.comentario.entity.Comentario;
import com.microservice.comentario.repository.ComentarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;


@Configuration
public class KafkaConsumerListener {

    @Autowired
    private ComentarioRepository comentarioRepository;

    private Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerListener.class);

    @KafkaListener(topics = {"Diego-topic"}, groupId = "my-group-id")
    public void listener(String message){

        LOGGER.info("Mensaje recibido, el mensaje es: " + message);

        List<Comentario> comentarioList = comentarioRepository.findAll();
        char opcion = message.charAt(0);
        int publicacionIdPrimitivo = Integer.parseInt(message.substring(2));
        Integer publicacionId = publicacionIdPrimitivo;

        switch (opcion){

            case 'A':
                for (Comentario comentario : comentarioList){
                    if (comentario.getPublicacionId().equals(publicacionId.longValue())){
                        comentarioRepository.delete(comentario);
                    }
                }
            break;
            default: LOGGER.info("No es Opcion -----------------------------------------");
        }


    }


}
