package com.jollyworks.kafkademo.consumers;

import java.time.Duration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class KafkaConsumerStartupListener {
    private final ReactiveKafkaTopicConsumer kafkaTopicConsumer;
    private final EmbeddingService embeddingService;

    public KafkaConsumerStartupListener(ReactiveKafkaTopicConsumer consumer, EmbeddingService embeddingService) { 
        this.kafkaTopicConsumer = consumer;
        this.embeddingService = embeddingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startConsumer() {
        this.kafkaTopicConsumer.consumeMessages()
        .bufferTimeout(10, Duration.ofSeconds(20)) // Process either 10 messages or after 20 seconds
        .flatMap(batch -> Flux.<String>fromIterable(batch)
            .parallel(4)
            .runOn(Schedulers.parallel())
            .flatMap(embeddingService::processMessage)
        )
        .doOnError(error -> System.err.println("Error processing message: " + error.getMessage()))
        .subscribe();
    }
}
