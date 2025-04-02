package com.jollyworks.kafkademo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;

import com.jollyworks.kafkademo.config.RssConfiguration;
import com.jollyworks.kafkademo.messages.RssItem;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;


@SpringBootTest
@ContextConfiguration(classes = {RssConfiguration.class})
public class RssConfigurationTest {
    
    @Autowired
    @Qualifier("items")
    private PollableChannel itemsChannel;


    @Test
    public void testRssConfiguration() {
        // This test will simply check if the Spring application context loads successfully
        // Set up the integration flow
        // Autowire the channel by its name "items" defined in the integration flow.
        // Poll the channel for a message (wait up to 2 seconds)
        Message<?> message = itemsChannel.receive(10000);
        assertNotNull(message, "Expected to receive an RSS feed message from the test resource");

        RssItem item = (RssItem) message.getPayload();
        
        assertEquals("Crane safety: New tool for addressing common hazards", item.getTitle());
        assertEquals("https://www.safetyandhealthmagazine.com/articles/26533-crane-safety-free-tool-for-addressing-common-hazards", item.getUrl());
        assertEquals("Tue Feb 25 00:00:00 EST 2025", item.getPubDate());


    }

}
