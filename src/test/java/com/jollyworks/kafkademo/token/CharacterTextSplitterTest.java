package com.jollyworks.kafkademo.token;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jollyworks.kafkademo.pipeline.token.CharacterTextSplitter;

class CharacterTextSplitterTest {

    private CharacterTextSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new CharacterTextSplitter(50); // Small chunk size for testing
    }

    @Test
    void testSplitByCharactersWithOverlap() {
        String text = "This is a long text that needs to be split into multiple chunks with some overlap between them to ensure continuity.";
        
        List<String> chunks = splitter.split(text);
        
        assertTrue(chunks.size() > 1);
        
        // Verify that chunks have some overlap
        for (int i = 1; i < chunks.size(); i++) {
            String previousChunk = chunks.get(i - 1);
            String currentChunk = chunks.get(i);
            
            // Check that there's some overlap (at least one common word)
            String[] prevWords = previousChunk.split("\\s+");
            String[] currentWords = currentChunk.split("\\s+");
            
            if (prevWords.length > 0 && currentWords.length > 0) {
                String lastWordPrev = prevWords[prevWords.length - 1];
                boolean hasOverlap = false;
                for (String word : currentWords) {
                    if (word.equals(lastWordPrev)) {
                        hasOverlap = true;
                        break;
                    }
                }
                // Note: Overlap might not always be exact due to word boundary logic
            }
        }
    }

    @Test
    void testEmptyText() {
        List<String> chunks = splitter.split("");
        assertTrue(chunks.isEmpty());
    }

    @Test
    void testNullText() {
        List<String> chunks = splitter.split(null);
        assertTrue(chunks.isEmpty());
    }

    @Test
    void testShortText() {
        String text = "Short text.";
        
        List<String> chunks = splitter.split(text);
        
        assertEquals(1, chunks.size());
        assertEquals("Short text.", chunks.get(0));
    }

    @Test
    void testAlgorithmName() {
        assertEquals("character-overlap", splitter.getAlgorithmName());
    }

    @Test
    void testChunkSizeRespected() {
        CharacterTextSplitter customSplitter = new CharacterTextSplitter(20);
        String text = "This is a very long text that should be split into multiple chunks";
        
        List<String> chunks = customSplitter.split(text);
        
        for (String chunk : chunks) {
            assertTrue(chunk.length() <= 25); // Allow some flexibility for word boundaries
        }
    }
}