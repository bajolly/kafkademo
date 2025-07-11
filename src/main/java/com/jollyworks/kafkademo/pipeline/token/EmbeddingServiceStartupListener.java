package com.jollyworks.kafkademo.pipeline.token;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmbeddingServiceStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final EmbeddingService embeddingService;

    public EmbeddingServiceStartupListener(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Starting Embedding Service...");
        embeddingService.startProcessing();
        log.info("Embedding Service started successfully");
    }
}