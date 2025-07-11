package com.jollyworks.kafkademo.pipeline.token;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CharacterTextSplitter implements TextSplitter {
    
    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final double OVERLAP_PERCENTAGE = 0.10; // 10% overlap
    
    private final int chunkSize;
    private final int overlapSize;
    
    public CharacterTextSplitter() {
        this(DEFAULT_CHUNK_SIZE);
    }
    
    public CharacterTextSplitter(int chunkSize) {
        this.chunkSize = chunkSize;
        this.overlapSize = (int) (chunkSize * OVERLAP_PERCENTAGE);
    }

    @Override
    public List<String> split(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        
        List<String> chunks = new ArrayList<>();
        int start = 0;
        
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            
            // Try to find a word boundary to avoid splitting words
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }
            
            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            
            // Calculate next start position with overlap
            start = Math.max(start + 1, end - overlapSize);
            
            // If we're at the end of the text, break
            if (end >= text.length()) {
                break;
            }
        }
        
        return chunks;
    }

    @Override
    public String getAlgorithmName() {
        return "character-overlap";
    }
}