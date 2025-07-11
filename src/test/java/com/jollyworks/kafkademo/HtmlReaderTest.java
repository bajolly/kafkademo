package com.jollyworks.kafkademo;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.jollyworks.kafkademo.pipeline.content.HtmlReader;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {HtmlReader.class})
class HtmlReaderTest {

    @Autowired
    private Function<Resource, List<Document>> htmlReaderFunction;

    @Test
    void testLoadHtmlText() throws TikaException {
        List<Document> documents = htmlReaderFunction.apply(new ClassPathResource("test.html"));

        // Assertions
        assertNotNull(documents);
        assertFalse(documents.isEmpty());

        // Validate extracted content
        String extractedContent = documents.get(0).getText();
        assertNotNull(extractedContent);
        assertTrue(extractedContent.contains("Hello World"), "Extracted content should contain 'Hello World'");
    }
}
