package com.jollyworks.kafkademo.pipeline.rss;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Profile("!test") // Prevents this listener from running in tests
public class KafkaProducerStartupListener {
    private final KafkaRSSProducer kafkaDemoProducer;

    public KafkaProducerStartupListener(KafkaRSSProducer producer) { 
        this.kafkaDemoProducer = producer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startProducer() { 
        // Get RSS feed data from <https://www.safetyandhealthmagazine.com/rss/topic/99-news>
        // for (int i = 1; i <= 120; i++) {
        //     kafkaDemoProducer.sendRoundRobinbMessageAsync("Interation" + i);
        //     try {
        //         Thread.sleep(1000); // Pause for 1 second
        //     } catch (InterruptedException e) {
        //         Thread.currentThread().interrupt(); // Restore interrupt flag
        //         System.err.println("Loop interrupted!");
        //         break;
        //     }
        // }
    }
}
