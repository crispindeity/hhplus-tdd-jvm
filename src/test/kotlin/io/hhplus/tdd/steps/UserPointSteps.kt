package io.hhplus.tdd.steps

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.http.MediaType

object UserPointSteps {
    fun chargeUserPoint(
        userId: Long,
        amount: Long
    ) {
        Given {
            contentType(MediaType.APPLICATION_JSON_VALUE)
            pathParam("id", userId)
            body(amount)
        } When {
            patch("/point/{id}/charge")
        } Then {
            statusCode(200)
        }
    }

    fun useUserPoint(
        userId: Long,
        amount: Long
    ) {
        Given {
            contentType(MediaType.APPLICATION_JSON_VALUE)
            pathParam("id", userId)
            body(amount)
        } When {
            patch("/point/{id}/use")
        } Then {
            statusCode(200)
        }
    }
}
