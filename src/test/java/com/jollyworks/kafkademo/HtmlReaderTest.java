package com.jollyworks.kafkademo;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jollyworks.kafkademo.ollama.HtmlReader;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class HtmlReaderTest {

    private HtmlReader htmlReader;

    @BeforeEach
    void setUp() throws IOException {
        // Load the real HTML file from resources
        Resource htmlresource = new ClassPathResource("test.html");
        htmlReader = new HtmlReader(htmlresource);
    }

    @Test
    void testLoadHtmlText() throws TikaException {
        List<Document> documents = htmlReader.loadText();

        // Assertions
        assertNotNull(documents);
        assertFalse(documents.isEmpty());

        // Validate extracted content
        String extractedContent = documents.get(0).getText();
        assertNotNull(extractedContent);
        assertTrue(extractedContent.contains("Hello World"), "Extracted content should contain 'Hello World'");
    }
}
