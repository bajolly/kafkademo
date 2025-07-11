package com.jollyworks.kafkademo.pipeline.content;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ContentProcessorStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ContentProcessor contentProcessor;

    public ContentProcessorStartupListener(ContentProcessor contentProcessor) {
        this.contentProcessor = contentProcessor;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Starting Content Processor...");
        contentProcessor.startProcessing();
        log.info("Content Processor started successfully");
    }
}