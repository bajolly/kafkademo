package com.jollyworks.kafkademo.pipeline.config;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.metadata.SimpleMetadataStore;

import com.jollyworks.kafkademo.pipeline.rss.dto.RssItem;
import com.jollyworks.kafkademo.platform.StringHashFunction;
import com.rometools.rome.feed.synd.SyndEntry;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableIntegration
@Slf4j
public class RssConfiguration {

    // Define an in-memory metadata store to track processed entries.
    //TODO this should be a persistent store
    @Bean
    public SimpleMetadataStore metadataStore() {
        return new SimpleMetadataStore();
    }

    @Bean
    public IntegrationFlow feedFlow(@Value("${rss.feed.url}") Resource feedResource,
            @Value("${rss.feed.duration:3600}") long durationsec,
            @Value("${rss.feed.maxMessagesPerPoll:5}") int maxMessagesPerPoll,
            SimpleMetadataStore metadataStore,
            StringHashFunction stringHashFunction) {

        Function<String, String> hashUniqueString = (String input) -> {
            try {
                return stringHashFunction.apply(input);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                log.warn("Error generating hash for input: " + input, e);
            }
            return input;
        };

        // Create a Duration object from the duration in seconds
        final Duration duration = Duration.ofSeconds(durationsec);

        // Manually create the FeedEntryMessageSource so that we can set the metadata
        // store.

        FeedEntryMessageSource feedEntryMessageSource = new FeedEntryMessageSource(feedResource,
                "SafetyHealthMagazine");
        feedEntryMessageSource.setPreserveWireFeed(true);
        feedEntryMessageSource.setMetadataStore(metadataStore);
        // Create a Feed object using the feedResource
        // and the duration every time the poller fires it will re-fetch the feed URL
        return IntegrationFlow
                .from(feedEntryMessageSource,
                        e -> e.poller(Pollers.fixedDelay(duration).maxMessagesPerPoll(maxMessagesPerPoll)))
                .<SyndEntry, RssItem>transform(s -> {
                    RssItem safetyHealthMagazine = new RssItem(
                            s.getUri(),
                            s.getTitle(),
                            s.getDescription().getValue(),
                            s.getLink(),
                            hashUniqueString.apply(s.getUri()),
                            s.getPublishedDate() != null ? s.getPublishedDate().toString() : null);
                    return safetyHealthMagazine;
                })
                .channel(c -> c.queue("items"))
                .get();
    }
}
