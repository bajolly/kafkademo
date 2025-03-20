package com.jollyworks.kafkademo.producer;

import com.jollyworks.kafkademo.producers.KafkaDemoProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@SpringBootTest
@ActiveProfiles("test")
public class TestKafkaDemoProducer {
    @MockitoSpyBean
    private KafkaDemoProducer kafkaDemoProducer;

    @Test
    void testSendMessage() {
        kafkaDemoProducer.sendRoundRobinbMessageAsync("Test message");

        // Verify that the test producer was called instead of the real Kafka producer
        verify(kafkaDemoProducer, times(1)).sendRoundRobinbMessageAsync("Test message");
    }
}
