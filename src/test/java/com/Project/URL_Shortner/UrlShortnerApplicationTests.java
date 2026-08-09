package com.Project.URL_Shortner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=test-only-secret-for-context-loads-0123456789abcdef")
@DisabledIfEnvironmentVariable(named = "CI", matches = "true",
		disabledReason = "Requires Postgres/Redis/Kafka services not provisioned in CI")
class UrlShortnerApplicationTests {

	@Test
	void contextLoads() {
	}

}
