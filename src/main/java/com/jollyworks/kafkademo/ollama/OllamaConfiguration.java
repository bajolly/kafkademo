package com.jollyworks.kafkademo.ollama;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfiguration{

    @Bean
    OllamaEmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi) {
        var ollamaOptions = OllamaOptions.builder()
                .model(OllamaModel.NOMIC_EMBED_TEXT)
                .build();
        return OllamaEmbeddingModel.builder()
                .defaultOptions(ollamaOptions)
                .ollamaApi(ollamaApi)
                .build();
    }
}
