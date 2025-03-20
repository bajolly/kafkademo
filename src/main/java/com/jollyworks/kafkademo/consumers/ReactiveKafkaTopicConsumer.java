package com.jollyworks.kafkademo.consumers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
@Service
public class ReactiveKafkaTopicConsumer {

    private final KafkaReceiver<String, String> receiver;

    public ReactiveKafkaTopicConsumer(ReceiverOptions<String, String> receiverOptions) {
        this.receiver = KafkaReceiver.create(receiverOptions);
    }

    public Flux<String> consumeMessages() {
        return receiver.receive()
                .map(ConsumerRecord::value)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
