package com.jollyworks.kafkademo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;


@SpringBootTest
@ActiveProfiles("test")
class KafkademoApplicationTests {

	@Autowired
    private Environment environment;

	@Test
	void contextLoads() {
		        // Verify that the active profile is "test"
				String[] activeProfiles = environment.getActiveProfiles();
		        boolean found = Arrays.stream(activeProfiles).anyMatch("test"::equals);
				assertTrue(found);
	}
}
