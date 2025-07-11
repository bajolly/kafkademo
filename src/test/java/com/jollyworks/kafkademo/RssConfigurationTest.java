package com.jollyworks.kafkademo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.jollyworks.kafkademo.pipeline.rss.dto.RssItem;
import com.jollyworks.kafkademo.pipeline.config.RssConfiguration;
import com.jollyworks.kafkademo.pipeline.config.Udf;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {RssConfiguration.class, Udf.class})
public class RssConfigurationTest {
    
    @Autowired
    IntegrationFlow feedFlow;

    @Autowired
    @Qualifier("items")
    private PollableChannel itemsChannel;

    
    @Test
    public void testRssFeed() {
        // Set up the integration flow
        // Autowire the channel by its name "items" defined in the integration flow.
        // Poll the channel for a message (wait up to 2 seconds)
        // Test poller will poll every second so we are also testing we only get new items on poll
        Message<?> message = itemsChannel.receive(2000);
        assertNotNull(message, "Expected to receive an RSS feed message from the test resource");

        RssItem item = (RssItem) message.getPayload();
        
        assertEquals("Crane safety: New tool for addressing common hazards", item.title());
        assertEquals("https://www.safetyandhealthmagazine.com/articles/26533-crane-safety-free-tool-for-addressing-common-hazards", item.url());
        assertEquals("Tue Feb 25 00:00:00 EST 2025", item.pubDate());

        //try {
        //    Thread.sleep(1500);  // what for the second poll
        //} catch (InterruptedException e) {
        //    assertFalse(true, "Test Interrupted");
        //}
        int count = 0;
        // Poll the channel for messages (wait up to 2 seconds)
        // This loop will keep receiving messages until the channel is empty
        while (true) {
            // Keep receiving messages until the channel is empty
            Message<?> nextMessage = itemsChannel.receive(2000);
            if (nextMessage == null) {
                break; // No more messages in the channel
            }
            count++;
        }
        assertEquals(2, count, "Expected to receive 2 messages from the test resource");
    }
}
