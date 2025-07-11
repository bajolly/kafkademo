package com.jollyworks.kafkademo.pipeline.token;

import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

//@Service
@Slf4j
public class KafkaDemoConsumer {

    @KafkaListener(topics = "rss-items-topic", groupId = "demo-group")
    public void listen(String message) {
        log.info("Received Message: " + message);
    }
}
