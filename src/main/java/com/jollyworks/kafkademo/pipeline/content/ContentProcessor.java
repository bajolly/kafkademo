package com.jollyworks.kafkademo.pipeline.content;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jollyworks.kafkademo.pipeline.content.dto.ContentItem;
import com.jollyworks.kafkademo.pipeline.rss.RssService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
@Service
public class ContentProcessor {
    
    private final KafkaTemplate<String, ContentItem> contentKafkaTemplate;
    private final RssService rssService;
    private final ObjectMapper objectMapper;
    private final KafkaReceiver<String, String> rssReceiver;
    
    public ContentProcessor(
            KafkaTemplate<String, ContentItem> contentKafkaTemplate,
            RssService rssService,
            ReceiverOptions<String, String> rssReactiveConsumerOptions) {
        this.contentKafkaTemplate = contentKafkaTemplate;
        this.rssService = rssService;
        this.objectMapper = new ObjectMapper();
        this.rssReceiver = KafkaReceiver.create(rssReactiveConsumerOptions);
    }
    
    public void startProcessing() {
        rssReceiver.receive()
            .flatMap(record -> {
                try {
                    JsonNode rssItemJson = objectMapper.readTree(record.value());
                    String id = rssItemJson.get("id").asText();
                    String title = rssItemJson.get("title").asText();
                    String url = rssItemJson.get("url").asText();
                    String pubDate = rssItemJson.get("pubDate").asText();
                    
                    log.info("Processing RSS item: {} - {}", id, title);
                    
                    return fetchAndPublishContent(id, title, url, pubDate)
                        .doOnSuccess(result -> record.receiverOffset().acknowledge())
                        .onErrorResume(error -> {
                            log.error("Error processing RSS item {}: {}", id, error.getMessage());
                            record.receiverOffset().acknowledge();
                            return Mono.empty();
                        });
                } catch (Exception e) {
                    log.error("Error parsing RSS message: {}", e.getMessage());
                    record.receiverOffset().acknowledge();
                    return Mono.empty();
                }
            })
            .subscribe();
    }
    
    private Mono<Void> fetchAndPublishContent(String id, String title, String url, String pubDate) {
        return Mono.fromCallable(() -> {
                String content = rssService.fetchHtmlContent(url);
                ContentItem contentItem = new ContentItem(
                    id,
                    title,
                    url,
                    content,
                    pubDate,
                    LocalDateTime.now()
                );
                
                contentKafkaTemplate.send(contentKafkaTemplate.getDefaultTopic(), contentItem);
                log.info("Published content for article: {} - {}", id, title);
                return contentItem;
            })
            .then();
    }
}