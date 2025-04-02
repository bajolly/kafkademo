package com.jollyworks.kafkademo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.feed.dsl.Feed;

import com.jollyworks.kafkademo.messages.RssItem;
import com.rometools.rome.feed.synd.SyndEntry;

@Configuration
@EnableIntegration
public class RssConfiguration {
    
    
    @Bean
    public IntegrationFlow feedFlow(@Value("${rss.feed.url}") Resource feedResource) {
        return IntegrationFlow
                .from(Feed.inboundAdapter(feedResource, "SafetyHealthMagazine")
                                .preserveWireFeed(true),
                        e -> e.poller(p -> p.fixedDelay(100)))
                .<SyndEntry, RssItem> transform(s -> {
                    RssItem safetyHealthMagazine = new RssItem(
                            s.getUri(),
                            s.getTitle(),
                            s.getDescription().getValue(),
                            s.getLink(),
                            s.getUri(),
                            s.getPublishedDate() != null ? s.getPublishedDate().toString() : null
                    );
                    return safetyHealthMagazine;
                })
                .channel(c -> c.queue("items"))
                .get();
    }
}
