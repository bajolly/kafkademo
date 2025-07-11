package com.jollyworks.kafkademo.config;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestVectorStoreConfiguration {

    @Bean
    @Primary
    public VectorStore sentenceVectorStore() {
        return mock(VectorStore.class);
    }

    @Bean
    @Primary 
    public VectorStore characterVectorStore() {
        return mock(VectorStore.class);
    }
}