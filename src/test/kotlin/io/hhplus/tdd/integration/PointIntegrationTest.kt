package io.hhplus.tdd.integration

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.steps.UserPointSteps
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

class PointIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var pointHistoryTable: PointHistoryTable

    @Autowired
    private lateinit var userPointTable: UserPointTable

    @BeforeEach
    fun setUp() {
        pointHistoryTable.clear()
        userPointTable.clear()
    }

    @Nested
    @DisplayName("유저 포인트 조회 통합 테스트")
    inner class RetrieveUserPointTest {
        @Nested
        @DisplayName("유저 포인트 조회 성공 통합 테스트")
        inner class RetrieveUserPointSuccessTest {
            @Test
            @DisplayName("유저 포인트를 조회 할 수 있어야 한다.")
            fun retrieveUserPointTest() {
                // given
                val userId = 1L

                UserPointSteps.chargeUserPoint(
                    userId = userId,
                    amount = 1000L
                )

                // when
                val response: Response =
                    Given {
                        log().all()
                        pathParam("id", userId)
                    } When {
                        get("/point/{id}")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(200)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo("success")
                    softAssertions
                        .assertThat(response.jsonPath().getLong("result.point"))
                        .isEqualTo(1000)
                }
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 충전/사용 내역 조회 통합 테스트")
    inner class RetrievePointHistoryTest {
        @Nested
        @DisplayName("유저 포인트 충전/사용 내역 조회 성공 통합 테스트")
        inner class RetrievePointHistorySuccessTest {
            @Test
            @DisplayName("유저의 포인트 충전/사용 내역을 조회 할 수 있어야 한다.")
            fun retrievePointHistoryTest() {
                // given
                val userId = 1L

                UserPointSteps.chargeUserPoint(
                    userId = userId,
                    amount = 1000L
                )

                UserPointSteps.useUserPoint(
                    userId = userId,
                    amount = 10L
                )

                // when
                val response: Response =
                    Given {
                        log().all()
                        pathParam("id", userId)
                    } When {
                        get("/point/{id}/histories")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(200)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo("success")
                    softAssertions
                        .assertThat(response.jsonPath().getLong("result.count"))
                        .isEqualTo(2)
                }
            }
        }

        @Nested
        @DisplayName("유저의 포인트 충전/사용 내역 조회 실패 통합 테스트")
        inner class RetrievePointHistoryFailTest {
            @Test
            @DisplayName("존재하지 않는 유저의 아이디로 포인트 충전 내역 조회 시 조회에 실패해야 한다.")
            fun retrievePointHistoryTest() {
                // given
                val wrongUserId = 1L

                UserPointSteps.chargeUserPoint(
                    userId = 2L,
                    amount = 1000L
                )

                // when
                val response: Response =
                    Given {
                        log().all()
                        pathParam("id", wrongUserId)
                    } When {
                        get("/point/{id}/histories")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(404)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo("not found user point history - $wrongUserId")
                }
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 충전 통합 테스트")
    inner class ChargeUserPointTest {
        @Nested
        @DisplayName("유저 포인트 충전 성공 통합 테스트")
        inner class ChargeUserPointSuccessTest {
            @Test
            @DisplayName("유저 포인트를 충전 할 수 있어야 한다.")
            fun chargeUserPointTest() {
                // given
                val userId = 1L
                val amount = 1000L

                // when
                val response: Response =
                    Given {
                        log().all()
                        contentType(MediaType.APPLICATION_JSON_VALUE)
                        pathParam("id", userId)
                        body(amount)
                    } When {
                        patch("/point/{id}/charge")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    val userPointHistory: List<PointHistory> =
                        pointHistoryTable.selectAllByUserId(userId)
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(200)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo("success")
                    softAssertions
                        .assertThat(response.jsonPath().getLong("result.id"))
                        .isEqualTo(userId)
                    softAssertions
                        .assertThat(response.jsonPath().getLong("result.point"))
                        .isEqualTo(amount)
                    softAssertions
                        .assertThat(userPointHistory)
                        .isNotEmpty
                    softAssertions
                        .assertThat(userPointHistory.first().type)
                        .isEqualTo(TransactionType.CHARGE)
                    softAssertions
                        .assertThat(userPointHistory.first().userId)
                        .isEqualTo(userId)
                    softAssertions
                        .assertThat(userPointHistory.first().amount)
                        .isEqualTo(amount)
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 충전 실패 테스트")
        inner class ChargeUserPointFailTest {
            @Test
            @DisplayName("충전 시 유저가 보유한 포인트가 최대 포인트 경우 충전에 실패해야 한다.")
            fun chargeUserPointTest() {
                // given
                val userId = 1L
                val amount = 2L
                val currentPoint = 999_999L

                userPointTable.insertOrUpdate(
                    id = userId,
                    amount = currentPoint
                )

                // when
                val response: Response =
                    Given {
                        log().all()
                        contentType(MediaType.APPLICATION_JSON_VALUE)
                        pathParam("id", userId)
                        body(amount)
                    } When {
                        patch("/point/{id}/charge")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(400)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo(
                            "user point exceeds allowed maximum - requestPoint: $amount, currentPoint: $currentPoint"
                        )
                }
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 사용 통합 테스트")
    inner class UseUserPointTest {
        @Nested
        @DisplayName("유저 포인트 사용 성공 통합 테스트")
        inner class UseUserPointSuccessTest {
            @Test
            @DisplayName("유저가 보유한 포인트를 사용할 수 있어야 한다.")
            fun useUserPointTest() {
                // given
                val userId = 1L
                val amount = 90L
                val currentPoint = 1000L

                UserPointSteps.chargeUserPoint(
                    userId = userId,
                    amount = currentPoint
                )

                // when
                val response: Response =
                    Given {
                        log().all()
                        contentType(MediaType.APPLICATION_JSON_VALUE)
                        pathParam("id", userId)
                        body(amount)
                    } When {
                        patch("/point/{id}/use")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    val userPointHistory: List<PointHistory> =
                        pointHistoryTable
                            .selectAllByUserId(userId)
                            .filter { it.type == TransactionType.USE }
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(200)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo("success")
                    softAssertions
                        .assertThat(response.jsonPath().getLong("result.id"))
                        .isEqualTo(userId)
                    softAssertions
                        .assertThat(response.jsonPath().getLong("result.point"))
                        .isEqualTo(currentPoint - amount)
                    softAssertions
                        .assertThat(userPointHistory.first().type)
                        .isEqualTo(TransactionType.USE)
                    softAssertions
                        .assertThat(userPointHistory.first().userId)
                        .isEqualTo(userId)
                    softAssertions
                        .assertThat(userPointHistory.first().amount)
                        .isEqualTo(amount)
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 사용 실패 통합 테스트")
        inner class UseUserPointFailTest {
            @Test
            @DisplayName("보유한 포인트보다 많이 사용하려는 경우 사용에 실패해야 한다.")
            fun useUserPointTest() {
                // given
                val userId = 1L
                val amount = 1001L
                val currentPoint = 1000L

                UserPointSteps.chargeUserPoint(
                    userId = userId,
                    amount = currentPoint
                )

                // when
                val response: Response =
                    Given {
                        log().all()
                        contentType(MediaType.APPLICATION_JSON_VALUE)
                        pathParam("id", userId)
                        body(amount)
                    } When {
                        patch("/point/{id}/use")
                    } Then {
                        log().all()
                        statusCode(200)
                    } Extract {
                        response()
                    }

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions
                        .assertThat(response.jsonPath().getShort("code"))
                        .isEqualTo(400)
                    softAssertions
                        .assertThat(response.jsonPath().getString("message"))
                        .isEqualTo(
                            "not enough point to complete the operation - requestPoint: $amount, currentPoint: $currentPoint"
                        )
                }
            }
        }
    }
}
