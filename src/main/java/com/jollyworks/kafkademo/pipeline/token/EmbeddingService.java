package com.jollyworks.kafkademo.pipeline.token;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jollyworks.kafkademo.pipeline.token.dto.TokenChunk;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
@Service
public class EmbeddingService {
    
    private final VectorStore sentenceVectorStore;
    private final VectorStore characterVectorStore;
    private final List<TextSplitter> textSplitters;
    private final ObjectMapper objectMapper;
    private final KafkaReceiver<String, String> contentReceiver;
    
    public EmbeddingService(
            @Qualifier("sentenceVectorStore") VectorStore sentenceVectorStore,
            @Qualifier("characterVectorStore") VectorStore characterVectorStore,
            List<TextSplitter> textSplitters,
            ReceiverOptions<String, String> contentReactiveConsumerOptions) {
        this.sentenceVectorStore = sentenceVectorStore;
        this.characterVectorStore = characterVectorStore;
        this.textSplitters = textSplitters;
        this.objectMapper = new ObjectMapper();
        this.contentReceiver = KafkaReceiver.create(contentReactiveConsumerOptions);
    }
    
    public void startProcessing() {
        contentReceiver.receive()
            .flatMap(record -> {
                try {
                    JsonNode contentJson = objectMapper.readTree(record.value());
                    String id = contentJson.get("id").asText();
                    String title = contentJson.get("title").asText();
                    String url = contentJson.get("url").asText();
                    String content = contentJson.get("content").asText();
                    String pubDate = contentJson.get("pubDate").asText();
                    
                    log.info("Processing content for embeddings: {} - {}", id, title);
                    
                    return processContentWithAllSplitters(id, title, url, content, pubDate)
                        .doOnSuccess(result -> record.receiverOffset().acknowledge())
                        .onErrorResume(error -> {
                            log.error("Error processing content {}: {}", id, error.getMessage());
                            record.receiverOffset().acknowledge();
                            return Mono.empty();
                        });
                } catch (Exception e) {
                    log.error("Error parsing content message: {}", e.getMessage());
                    record.receiverOffset().acknowledge();
                    return Mono.empty();
                }
            })
            .subscribe();
    }
    
    private Mono<Void> processContentWithAllSplitters(String contentId, String title, String url, 
                                                      String content, String pubDate) {
        return Mono.fromRunnable(() -> {
            for (TextSplitter splitter : textSplitters) {
                try {
                    List<String> chunks = splitter.split(content);
                    log.info("Split content {} into {} chunks using {} algorithm", 
                            contentId, chunks.size(), splitter.getAlgorithmName());
                    
                    for (int i = 0; i < chunks.size(); i++) {
                        String chunkText = chunks.get(i);
                        TokenChunk tokenChunk = createTokenChunk(contentId, chunkText, i, 
                                                                splitter.getAlgorithmName(), 
                                                                title, url, pubDate);
                        storeEmbedding(tokenChunk);
                    }
                } catch (Exception e) {
                    log.error("Error processing content {} with splitter {}: {}", 
                             contentId, splitter.getAlgorithmName(), e.getMessage());
                }
            }
        });
    }
    
    private TokenChunk createTokenChunk(String contentId, String chunkText, int chunkIndex, 
                                       String algorithm, String title, String url, String pubDate) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("title", title);
        metadata.put("url", url);
        metadata.put("pubDate", pubDate);
        metadata.put("contentId", contentId);
        metadata.put("chunkIndex", chunkIndex);
        metadata.put("splitterAlgorithm", algorithm);
        
        return new TokenChunk(
            UUID.randomUUID().toString(),
            contentId,
            chunkText,
            chunkIndex,
            algorithm,
            metadata,
            LocalDateTime.now()
        );
    }
    
    private void storeEmbedding(TokenChunk tokenChunk) {
        try {
            Map<String, Object> metadata = new HashMap<>(tokenChunk.metadata());
            metadata.put("id", tokenChunk.id());
            
            Document document = new Document(tokenChunk.id(), tokenChunk.chunkText(), metadata);
            
            VectorStore targetVectorStore = getVectorStoreForAlgorithm(tokenChunk.splitterAlgorithm());
            targetVectorStore.add(List.of(document));
            log.debug("Stored embedding for chunk {} in {} collection (algorithm: {})", 
                     tokenChunk.id(), getCollectionName(tokenChunk.splitterAlgorithm()), tokenChunk.splitterAlgorithm());
        } catch (Exception e) {
            log.error("Error storing embedding for chunk {}: {}", tokenChunk.id(), e.getMessage());
        }
    }
    
    VectorStore getVectorStoreForAlgorithm(String algorithm) {
        switch (algorithm) {
            case "sentence":
                return sentenceVectorStore;
            case "character-overlap":
                return characterVectorStore;
            default:
                log.warn("Unknown algorithm {}, defaulting to sentence vector store", algorithm);
                return sentenceVectorStore;
        }
    }
    
    String getCollectionName(String algorithm) {
        switch (algorithm) {
            case "sentence":
                return "SentenceCollection";
            case "character-overlap":
                return "CharacterCollection";
            default:
                return "SentenceCollection";
        }
    }
}
