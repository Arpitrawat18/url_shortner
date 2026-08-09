package com.Project.URL_Shortner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=test-only-secret-for-context-loads-0123456789abcdef")
class UrlShortnerApplicationTests {

	@Test
	void contextLoads() {
	}

}
