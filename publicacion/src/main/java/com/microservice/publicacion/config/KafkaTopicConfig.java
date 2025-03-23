package com.microservice.publicacion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {



    @Bean
    public NewTopic generateTopic(){

        Map<String, String> configurations = new HashMap<>();
        configurations.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE); //(DELETE) borra el mensaje, (COMPACT) mantiene el mas actual
        configurations.put(TopicConfig.RETENTION_MS_CONFIG, "86400000"); // tiempo que durara el mensaje - si no se coloca no borra mensaje -1: valor por defecto
        configurations.put(TopicConfig.SEGMENT_BYTES_CONFIG, "1073741824"); // tamaño maximo del segmento colocado en bytes osea 1073741824 son 1GB
        configurations.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, "1000012"); // tamaño maximo de cada mensaje, por defecto viene en 1MB

        return TopicBuilder
                .name("Diego-topic")
                .partitions(2)
                .replicas(2)
                .configs(configurations)
                .build();
    }

}
