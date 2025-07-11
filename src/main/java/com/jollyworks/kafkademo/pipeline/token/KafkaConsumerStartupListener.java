package com.jollyworks.kafkademo.pipeline.token;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumerStartupListener {
    private final ReactiveKafkaTopicConsumer kafkaTopicConsumer;

    public KafkaConsumerStartupListener(ReactiveKafkaTopicConsumer consumer) { 
        this.kafkaTopicConsumer = consumer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startConsumer() {
        log.info("Starting legacy RSS topic consumer for backwards compatibility");
        // This consumer is kept for backwards compatibility with existing RSS topic consumption
        // The new processing pipeline uses dedicated content processors and embedding services
        this.kafkaTopicConsumer.consumeMessages()
            .doOnNext(message -> log.debug("Received legacy RSS message: {}", message))
            .doOnError(error -> log.error("Error processing legacy RSS message: {}", error.getMessage()))
            .subscribe();
    }
}
