package io.hhplus.tdd.integration

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.web.server.LocalServerPort

@IntegrationTest
abstract class AbstractIntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @BeforeAll
    fun initRestAssuredPort() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port
        }
    }
}
