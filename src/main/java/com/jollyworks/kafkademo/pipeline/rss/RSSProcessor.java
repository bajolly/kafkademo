package com.jollyworks.kafkademo.pipeline.rss;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import com.jollyworks.kafkademo.pipeline.rss.dto.RssItem;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RSSProcessor {

    private final KafkaRSSProducer kafkaRSSProducer;

    public RSSProcessor(KafkaRSSProducer kafkaRSSProducer) {
        this.kafkaRSSProducer = kafkaRSSProducer;
    }

    @ServiceActivator(inputChannel = "items")
    public void processRssItem(RssItem rssItem) {
        log.info("Processing RSS item: {}", rssItem.title());
        
        try {
            kafkaRSSProducer.sendRoundRobinbMessageAsync(rssItem);
            log.info("Successfully published RSS item to Kafka: {}", rssItem.title());
        } catch (Exception e) {
            log.error("Failed to publish RSS item to Kafka: {}", rssItem.title(), e);
        }
    }
}