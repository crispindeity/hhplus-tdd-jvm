package io.hhplus.tdd.point

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.tdd.fake.FakePointHistoryQueryUseCase
import io.hhplus.tdd.fake.FakeUserPointCommandUseCase
import io.hhplus.tdd.fake.FakeUserPointQueryUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@Import(
    FakeUserPointQueryUseCase::class,
    FakePointHistoryQueryUseCase::class,
    FakeUserPointCommandUseCase::class
)
@WebMvcTest(PointController::class)
class PointControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    @Nested
    @DisplayName("유저 포인트 조회 요청 테스트")
    inner class RetrieveUserPointApiTest {
        @Nested
        @DisplayName("유저 포인트 조회 요청 성공 테스트")
        inner class RetrieveUserPointApiSuccessTest {
            @Test
            @DisplayName("특정 유저의 포인트를 조회 요청 할 수 있어야 한다.")
            fun retrieveUserPointTest() {
                // given
                val userId = 1L

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .get("/point/{id}", userId)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(200),
                        MockMvcResultMatchers.jsonPath("$.message").value("success")
                    )
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 내역 조회 요청 테스트")
    inner class RetrievePointHistoryApiTest {
        @Nested
        @DisplayName("유저 포인트 내역 조회 요청 성공 테스트")
        inner class RetrievePointHistoryApiSuccessTest {
            @Test
            @DisplayName("특정 유저의 포인트 충전/사용 내역 조회를 요청 할 수 있어야 한다.")
            fun retrievePointHistoryTest() {
                // given
                val userId = 1L

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .get("/point/{id}/histories", userId)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(200),
                        MockMvcResultMatchers.jsonPath("$.message").value("success")
                    )
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 충전 요청 테스트")
    inner class ChargePointApiTest {
        @Nested
        @DisplayName("유저 포인트 충전 요청 성공 테스트")
        inner class ChargePointApiSuccessTest {
            @Test
            @DisplayName("특정 유저의 포인트 충전을 요청 할 수 있어야 한다.")
            fun chargePointTest() {
                // given
                val userId = 1L
                val amount = 1000L

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .patch("/point/{id}/charge", userId)
                                .content(objectMapper.writeValueAsString(amount))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(200),
                        MockMvcResultMatchers.jsonPath("$.message").value("success")
                    )
            }
        }

        @Nested
        @DisplayName("유저 포인트 충전 요청 실패 테스트")
        inner class ChargePointApiFailTest {
            @Test
            @DisplayName("특정 유저의 포인트 충전 요청 시 포인트의 수량이 0이면 요청에 실패해야 한다.")
            fun chargePointTest() {
                // given
                val userId = 1L
                val amount = 0

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .patch("/point/{id}/charge", userId)
                                .content(objectMapper.writeValueAsString(amount))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(400),
                        MockMvcResultMatchers.jsonPath("$.message").value("invalid request value"),
                        MockMvcResultMatchers.jsonPath("$.result.errors[0].field").value("amount"),
                        MockMvcResultMatchers.jsonPath("$.result.errors[0].value").value(amount)
                    )
            }

            @Test
            @DisplayName("특정 유저의 포인트 충전 요청 시 포인트의 수량이 음수면 요청에 실패해야 한다.")
            fun chargePointTest2() {
                // given
                val userId = 1L
                val amount = -100

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .patch("/point/{id}/charge", userId)
                                .content(objectMapper.writeValueAsString(amount))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(400),
                        MockMvcResultMatchers.jsonPath("$.message").value("invalid request value"),
                        MockMvcResultMatchers.jsonPath("$.result.errors[0].field").value("amount"),
                        MockMvcResultMatchers.jsonPath("$.result.errors[0].value").value(amount)
                    )
            }

            @Test
            @DisplayName("특정 유저의 포인트 충전 요청 시 포인트의 수량이 보유할 수 있는 포인트 보다 많으면 요청에 실패해야 한다.")
            fun chargePointTest3() {
                // given
                val userId = 1L
                val amount = 1_000_000

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .patch("/point/{id}/charge", userId)
                                .content(objectMapper.writeValueAsString(amount))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(400),
                        MockMvcResultMatchers.jsonPath("$.message").value("invalid request value"),
                        MockMvcResultMatchers.jsonPath("$.result.errors[0].field").value("amount"),
                        MockMvcResultMatchers.jsonPath("$.result.errors[0].value").value(amount)
                    )
            }
        }
    }

    @Nested
    @DisplayName("유저 포인트 사용 요청 테스트")
    inner class UsePointApiTest {
        @Nested
        @DisplayName("유저 포인트 사용 요청 성공 테스트")
        inner class UsePointApiSuccessTest {
            @Test
            @DisplayName("특정 유저의 포인트를 사용할 수 있어야 한다.")
            fun usePointTest() {
                // given
                val userId = 1L
                val amount = 1000L

                // when
                val result: ResultActions =
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders
                                .patch("/point/{id}/use", userId)
                                .content(objectMapper.writeValueAsString(amount))
                                .contentType(MediaType.APPLICATION_JSON)
                        ).andDo(MockMvcResultHandlers.print())

                // then
                result
                    .andExpectAll(
                        MockMvcResultMatchers.status().isOk,
                        MockMvcResultMatchers.jsonPath("$.code").value(200),
                        MockMvcResultMatchers.jsonPath("$.message").value("success")
                    )
            }
        }
    }
}
