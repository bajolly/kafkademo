package com.jollyworks.kafkademo.producers;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaProducerListener implements ProducerListener<String, String>{

    @Override
    public void onError(ProducerRecord<String, String> producerRecord, @Nullable RecordMetadata recordMetadata, Exception exception) {
        log.error(String.format("Failed to write record to Topic %s partion %d key %s",
        producerRecord.topic(),
        producerRecord.partition(),
        producerRecord.key()), exception);
    }
     

    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
        log.info(String.format(String.format("Successfully wrote record to Topic %s Partion %d Key %s with Value %s",
        recordMetadata.topic(),
        recordMetadata.partition(),
        producerRecord.key(),
        producerRecord.value())));
    }
}
