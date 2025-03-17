package com.jollyworks.kafkademo.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.jollyworks.kafkademo.producers.KafkaProducerListener;

@Configuration
public class KafkaConfiguration {
    

    @Scope("singleton")
    @Bean
    NewTopic chunksTopic(){
        return TopicBuilder.name("resume-chunks-topic")
        .partitions(1)
        .replicas(1)
        .build();
    }


    @Bean
    ProducerFactory<String, String> producerFactory(String kafka_server) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafka_server));
    }

    @Bean
    Map<String, Object> producerConfigs(String kafka_server) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(@Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafka_server) {
        var kt = new KafkaTemplate<String, String>(producerFactory(kafka_server));
        // THIS is for learning only in current implementation this will result in double logging
        // since Producer also logs results of send method
        kt.setProducerListener(new KafkaProducerListener());
        kt.setDefaultTopic(chunksTopic().name());
        return kt;
    }
}
