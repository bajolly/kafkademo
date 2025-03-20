package com.jollyworks.kafkademo.ollama;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class HtmlReader {
        private final Resource resource;
    
        public HtmlReader(@Value("classpath:/example.html") Resource resource) { 
            this.resource = resource;
        }

        public List<Document> loadText() { 
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            return tikaDocumentReader.read();
        }

}
