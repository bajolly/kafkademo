package com.jollyworks.pipeline;

import org.junit.jupiter.api.Test;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import com.jollyworks.kafkademo.config.TestVectorStoreConfiguration;
import com.jollyworks.kafkademo.pipeline.KafkaDemoApplication;
import com.jollyworks.kafkademo.platform.Gate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;


@SpringBootTest(classes = KafkaDemoApplication.class)
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@ActiveProfiles("test")
@Import(TestVectorStoreConfiguration.class)
class KafkademoApplicationTests {

	@Autowired
    private Environment environment;

	@Test
	void contextLoads() {
		        // Verify that the active profile is "test"
				String[] activeProfiles = environment.getActiveProfiles();
		        boolean found = Arrays.stream(activeProfiles).anyMatch("test"::equals);
				Gate.open("ExitApplication");
				assertTrue(found);
	}
}
