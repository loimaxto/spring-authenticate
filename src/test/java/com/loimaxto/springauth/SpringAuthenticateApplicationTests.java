package com.loimaxto.springauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never",
    "spring.security.oauth2.client.registration.google.client-id=test-google-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-google-client-secret",
    "spring.security.oauth2.client.registration.facebook.client-id=test-facebook-client-id",
    "spring.security.oauth2.client.registration.facebook.client-secret=test-facebook-client-secret",
    "spring.security.oauth2.client.registration.github.client-id=test-github-client-id",
    "spring.security.oauth2.client.registration.github.client-secret=test-github-client-secret"
})
class SpringAuthenticateApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
    }
}
