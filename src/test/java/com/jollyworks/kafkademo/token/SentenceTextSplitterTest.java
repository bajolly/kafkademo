package com.jollyworks.kafkademo.token;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jollyworks.kafkademo.pipeline.token.SentenceTextSplitter;

class SentenceTextSplitterTest {

    private SentenceTextSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new SentenceTextSplitter();
    }

    @Test
    void testSplitBySentences() {
        String text = "This is the first sentence. This is the second sentence! Is this the third sentence? Yes, it is.";
        
        List<String> chunks = splitter.split(text);
        
        assertEquals(4, chunks.size());
        assertEquals("This is the first sentence.", chunks.get(0));
        assertEquals("This is the second sentence!", chunks.get(1));
        assertEquals("Is this the third sentence?", chunks.get(2));
        assertEquals("Yes, it is.", chunks.get(3));
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
    void testSingleSentence() {
        String text = "This is a single sentence.";
        
        List<String> chunks = splitter.split(text);
        
        assertEquals(1, chunks.size());
        assertEquals("This is a single sentence.", chunks.get(0));
    }

    @Test
    void testAlgorithmName() {
        assertEquals("sentence", splitter.getAlgorithmName());
    }
}