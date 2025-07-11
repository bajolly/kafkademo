package com.jollyworks.kafkademo.pipeline.token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class SentenceTextSplitter implements TextSplitter {

    @Override
    public List<String> split(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        
        // Split by sentence-ending punctuation followed by whitespace
        String[] sentences = text.split("(?<=[.!?])\\s+");
        
        return Arrays.stream(sentences)
                .map(String::trim)
                .filter(sentence -> !sentence.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String getAlgorithmName() {
        return "sentence";
    }
}