package com.jollyworks.kafkademo.pipeline.content;

import java.util.List;
import java.util.function.Function;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class HtmlReader {

    @Bean
    public Function<Resource, List<Document>> htmlLoadText() {
        return resource -> {
        try {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            List<Document> documents = tikaDocumentReader.read();
            return documents;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read HTML content from resource", e);
        }
    };
}
}
