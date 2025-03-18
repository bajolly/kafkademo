package com.jollyworks.kafkademo.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaDemoConsumer {

    @KafkaListener(topics = "resume-chunks-topic", groupId = "demo-group")
    public void listen(String message) {
        log.info("Received Message: " + message);
    }
}
