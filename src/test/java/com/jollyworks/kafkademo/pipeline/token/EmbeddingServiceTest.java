package com.jollyworks.kafkademo.pipeline.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import com.jollyworks.kafkademo.pipeline.content.dto.ContentItem;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmbeddingServiceTest {

    @Mock
    private VectorStore sentenceVectorStore;

    @Mock
    private VectorStore characterVectorStore;

    @Mock
    private TextSplitter sentenceTextSplitter;

    @Mock
    private TextSplitter characterTextSplitter;

    @Mock
    private ReceiverOptions<String, ContentItem> receiverOptions;

    private EmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        List<TextSplitter> textSplitters = Arrays.asList(sentenceTextSplitter, characterTextSplitter);
        
        embeddingService = new EmbeddingService(
            sentenceVectorStore,
            characterVectorStore,
            textSplitters,
            receiverOptions
        );
    }

    @Test
    void testGetVectorStoreForSentenceAlgorithm() {
        // Use reflection or create a public method to test the algorithm routing
        VectorStore result = embeddingService.getVectorStoreForAlgorithm("sentence");
        assertEquals(sentenceVectorStore, result);
    }

    @Test
    void testGetVectorStoreForCharacterAlgorithm() {
        VectorStore result = embeddingService.getVectorStoreForAlgorithm("character-overlap");
        assertEquals(characterVectorStore, result);
    }

    @Test
    void testGetVectorStoreForUnknownAlgorithmDefaultsToSentence() {
        VectorStore result = embeddingService.getVectorStoreForAlgorithm("unknown");
        assertEquals(sentenceVectorStore, result);
    }

    @Test
    void testGetCollectionNameForSentenceAlgorithm() {
        String result = embeddingService.getCollectionName("sentence");
        assertEquals("SentenceCollection", result);
    }

    @Test
    void testGetCollectionNameForCharacterAlgorithm() {
        String result = embeddingService.getCollectionName("character-overlap");
        assertEquals("CharacterCollection", result);
    }

    @Test
    void testGetCollectionNameForUnknownAlgorithmDefaultsToSentence() {
        String result = embeddingService.getCollectionName("unknown");
        assertEquals("SentenceCollection", result);
    }
}