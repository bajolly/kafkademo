package com.jollyworks.kafkademo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.Order;

import com.jollyworks.kafkademo.consumers.KafkaDemoConsumer;
import com.jollyworks.kafkademo.producers.KafkaDemoProducer;

@SpringBootApplication
public class KafkademoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkademoApplication.class, args);

		// run until receives a break signal
		while (true) {

		}
	}


	@Order(0)
	@Bean
	public ApplicationRunner producerRunner(KafkaDemoProducer demoProducer) { 

			return args -> {
				demoProducer.sendRoundRobinbMessageAsync("testing");
				// If this is a blocking call consumer runner won't run 
				// until after producerRunner completes
			};
	}

	@Order()
	@Bean ApplicationRunner consumerRunner(KafkaDemoConsumer demoConsumer) { 
		return null;
	}

}
