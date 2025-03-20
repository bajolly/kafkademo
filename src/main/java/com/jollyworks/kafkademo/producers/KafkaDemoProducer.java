package com.jollyworks.kafkademo.producers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaDemoProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaDemoProducer(KafkaTemplate<String, String> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRoundRobinbMessageAsync(String message) {
        // don't need have there records do to the same partition
        ProducerRecord<String, String> pr = createRecord(message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(pr);
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


    public void sendRoundRobinbMessageBlock(String message) {
        // don't need have there records do to the same partition
        ProducerRecord<String, String> pr = createRecord(message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(pr);
        
        try {
            SendResult<String, String> result = future.get();
            log.info("Message sent to topic %s and partition %d", 
            result.getRecordMetadata().topic(),
            result.getRecordMetadata().partition());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ProducerRecord<String, String> createRecord(String msg) {
        var defaultTopic = kafkaTemplate.getDefaultTopic();
        var pr = new ProducerRecord<String, String>(defaultTopic, msg);
        return pr;
    }
}
