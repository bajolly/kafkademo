package com.jollyworks.kafkademo.pipeline.token;

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

    public ReactiveKafkaTopicConsumer(ReceiverOptions<String, String> rssReactiveConsumerOptions) {
        this.receiver = KafkaReceiver.create(rssReactiveConsumerOptions);
    }

    public Flux<String> consumeMessages() {
        return receiver.receive()
                .map(ConsumerRecord::value)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
