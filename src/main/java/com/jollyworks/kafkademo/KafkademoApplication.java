package com.jollyworks.kafkademo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jollyworks.kafkademo.producers.KafkaDemoProducer;

@SpringBootApplication
public class KafkademoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkademoApplication.class, args);

		// run until receives a break signal
		while (true) {

		}
	}


	@Bean
	ApplicationRunner producerRunner(KafkaDemoProducer demoProducer) { 

			return args -> {
				demoProducer.sendRoundRobinbMessageAsync("testing");
				// If this is a blocking call consumer runner won't run 
				// until after producerRunner completes
			};
	}
}
