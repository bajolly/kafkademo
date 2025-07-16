package com.jollyworks.kafkademo.pipeline.config;

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

import com.jollyworks.kafkademo.pipeline.rss.dto.RssItem;
import com.jollyworks.kafkademo.pipeline.content.dto.ContentItem;
import com.jollyworks.kafkademo.pipeline.rss.KafkaProducerListener;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

@Configuration
public class KafkaConfiguration {

    @Scope("singleton")
    @Bean
    NewTopic rssItemsTopic() {
        return TopicBuilder.name("rss-items-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Scope("singleton")
    @Bean
    NewTopic contentTopic() {
        return TopicBuilder.name("content-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    ProducerFactory<String, RssItem> rssProducerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafkaServer));
    }

    @Bean
    ProducerFactory<String, ContentItem> contentProducerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafkaServer));
    }

    @Bean
    Map<String, Object> producerConfigs(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // See https://kafka.apache.org/documentation/#producerconfigs for more
        // properties
        return props;
    }

    @Bean
    KafkaTemplate<String, RssItem> rssKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        var kt = new KafkaTemplate<String, RssItem>(rssProducerFactory(kafkaServer));
        kt.setProducerListener(new KafkaProducerListener());
        kt.setDefaultTopic(rssItemsTopic().name());
        return kt;
    }

    @Bean
    KafkaTemplate<String, ContentItem> contentKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        var kt = new KafkaTemplate<String, ContentItem>(contentProducerFactory(kafkaServer));
        kt.setDefaultTopic(contentTopic().name());
        return kt;
    }

    ConsumerFactory<String, String> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(kafkaServer));
    }

    @Bean
    ReceiverOptions<String, String> rssReactiveConsumerOptions(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        var props = consumerConfig(kafkaServer);
        return ReceiverOptions.<String, String>create(props)
                .subscription(Collections.singleton("rss-items-topic"))
                .commitBatchSize(100)
                .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToBeginning));
    }

    @Bean
    ReceiverOptions<String, ContentItem> contentReactiveConsumerOptions(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        var props = contentConsumerConfig(kafkaServer);
        return ReceiverOptions.<String, ContentItem>create(props)
                .subscription(Collections.singleton("content-topic"))
                .commitBatchSize(100)
                .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToBeginning));
    }

    Map<String, Object> consumerConfig(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "rss-reactive-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return props;
    }

    Map<String, Object> contentConsumerConfig(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String kafkaServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "content-reactive-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // Configure JsonDeserializer to trust the ContentItem package
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.jollyworks.kafkademo.pipeline.content.dto");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ContentItem.class.getName());
        return props;
    }
}
