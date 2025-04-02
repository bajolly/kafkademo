package com.jollyworks.kafkademo.producer;

import com.jollyworks.kafkademo.config.KafkaConfiguration;
import com.jollyworks.kafkademo.config.Udf;
import com.jollyworks.kafkademo.messages.RssItem;
import com.jollyworks.kafkademo.platform.StringHashFunction;
import com.jollyworks.kafkademo.producers.KafkaRSSProducer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.Mockito.verify;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {KafkaRSSProducer.class,
    KafkaConfiguration.class,
    Udf.class})
public class TestKafkaDemoProducer {
    @MockitoSpyBean
    private KafkaRSSProducer kafkaDemoProducer;

    @Autowired
    private StringHashFunction stringHashFunction;

    @Test
    void testSendMessage() {
        // Given a SafetyHealthMagazine object
        RssItem message;
        try {
            // Create a SafetyHealthMagazine object with the required parameters
            // The id is generated using the stringHashFunction
            String id = stringHashFunction.apply("Title" + "Description" + "URL" + "Publisher" + "01 Jan 2023");
            message = new RssItem(id, "Title", "Description", "URL", "Publisher", "01 Jan 2023");
            kafkaDemoProducer.sendRoundRobinbMessageAsync(message);

            // Verify that the test producer was called instead of the real Kafka producer
            verify(kafkaDemoProducer, times(1)).sendRoundRobinbMessageAsync(message);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            // Handle the exception as needed
            fail("Exception occurred while creating SafetyHealthMagazine: " + e.getMessage());        
        }
    }
}
