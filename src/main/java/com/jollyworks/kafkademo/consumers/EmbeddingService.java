package com.jollyworks.kafkademo.consumers;


import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class EmbeddingService {
    
    public Mono<Void> processMessage(String message) { 
        return Mono.fromRunnable(
            () -> { 
                log.info(String.format("Embedding %s", message)); // This will be the embedding Function
            }).then();
    }
}
