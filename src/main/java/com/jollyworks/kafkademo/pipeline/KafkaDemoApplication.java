package com.jollyworks.kafkademo.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jollyworks.kafkademo.platform.GatedSubscriber;

import reactor.core.publisher.Mono;


@SpringBootApplication
public class KafkaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);

		Mono<String> waitForExit = Mono.just("Successful Exit");
		
		// Call Gate.open("ExitApplication") somewhere in the code to open the gate
		// the subscriber will wait until gate is opened before processing items
		waitForExit.subscribe(new GatedSubscriber<>("ExitApplication"));
		
	}
}
