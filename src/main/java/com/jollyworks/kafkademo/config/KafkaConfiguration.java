package com.jollyworks.kafkademo.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.jollyworks.kafkademo.messages.RssItem;
import com.jollyworks.kafkademo.producers.KafkaProducerListener;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

@Configuration
public class KafkaConfiguration {

    @Scope("singleton")
    @Bean
    NewTopic chunksTopic() {
        return TopicBuilder.name("resume-chunks-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    ProducerFactory<String, RssItem> producerFactory(String kafka_server) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafka_server));
    }

    @Bean
    Map<String, Object> producerConfigs(String kafka_server) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // See https://kafka.apache.org/documentation/#producerconfigs for more
        // properties
        return props;
    }

    @Bean
    KafkaTemplate<String, RssItem> kafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        var kt = new KafkaTemplate<String, RssItem>(producerFactory(kafkaServer));
        // THIS is for learning only in current implementation this will result in
        // double logging
        // since Producer also logs results of send method
        kt.setProducerListener(new KafkaProducerListener());
        kt.setDefaultTopic(chunksTopic().name());
        return kt;
    }

    ConsumerFactory<String, String> consumerFactory(String kafkaServer) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(kafkaServer));
    }

    @Bean
    ReceiverOptions<String, String> reactiveConsumerOptions(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        var props = consumerConfig(kafkaServer);
        return ReceiverOptions.<String, String>create(props)
                .subscription(Collections.singleton("resume-chunks-topic"))
                .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToBeginning)); // Seek to
                                                                                                          // beginning
                                                                                                          // for new
                                                                                                          // consumers
    }

    Map<String, Object> consumerConfig(String kafkaServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "reactive-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return props;
    }
}
