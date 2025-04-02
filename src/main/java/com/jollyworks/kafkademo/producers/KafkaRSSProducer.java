package com.jollyworks.kafkademo.producers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.jollyworks.kafkademo.messages.RssItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaRSSProducer {

    private final KafkaTemplate<String, RssItem> kafkaTemplate;

    public KafkaRSSProducer(KafkaTemplate<String, RssItem> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRoundRobinbMessageAsync(RssItem message) {
        // don't need have there records do to the same partition
        ProducerRecord<String, RssItem> pr = createRecord(message);
        CompletableFuture<SendResult<String, RssItem>> future = kafkaTemplate.send(pr);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(String.format("Message sent to topic %s and partition %d", 
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition()));
            }
            else { 
                log.error(ex.getMessage(), ex);
            }
        });
    }


    public void sendRoundRobinbMessageBlock(RssItem message) {
        // don't need have there records do to the same partition
        ProducerRecord<String, RssItem> pr = createRecord(message);
        CompletableFuture<SendResult<String, RssItem>> future = kafkaTemplate.send(pr);
        
        try {
            SendResult<String, RssItem> result = future.get();
            log.info("Message sent to topic %s and partition %d", 
            result.getRecordMetadata().topic(),
            result.getRecordMetadata().partition());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ProducerRecord<String, RssItem> createRecord(RssItem msg) {
        var defaultTopic = kafkaTemplate.getDefaultTopic();
        var pr = new ProducerRecord<String, RssItem>(defaultTopic, msg);
        return pr;
    }
}
