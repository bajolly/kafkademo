package com.jollyworks.kafkademo.pipeline.config;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ChromaConfiguration {

    @Value("${spring.ai.vectorstore.chroma.client.host:localhost}")
    String host;
    @Value("${spring.ai.vectorstore.chroma.client.port:8000}")
    int port;

    @Bean
    RestClient.Builder builder() {
        return RestClient.builder().requestFactory(new SimpleClientHttpRequestFactory());
    }


    @Bean
    ChromaApi chromaApi(RestClient.Builder restClientBuilder) {
        String chromaUrl = String.join("","http:","//",host,":",Integer.toString(port));
        ChromaApi chromaApi = new ChromaApi(chromaUrl, restClientBuilder);
        return chromaApi;
    }

    @Bean
    @Profile("!test")
    VectorStore sentenceVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
            .collectionName("SentenceCollection")
            .initializeSchema(true)
            .build();
    }

    @Bean
    @Profile("!test")
    VectorStore characterVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
            .collectionName("CharacterCollection")
            .initializeSchema(true)
            .build();
    }
}
