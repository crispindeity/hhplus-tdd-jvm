package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode
import io.hhplus.tdd.fake.FakeUserPointQueryPort
import io.hhplus.tdd.point.port.input.UserPointQueryUseCase
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointServiceTest {
    private lateinit var pointService: PointService
    private lateinit var fakeUserPointQueryPort: FakeUserPointQueryPort

    @BeforeEach
    fun setUp() {
        fakeUserPointQueryPort = FakeUserPointQueryPort()
        pointService =
            PointService(
                userPointQueryPort = fakeUserPointQueryPort
            )
    }

    @Nested
    @DisplayName("유저 포인트 조회 조회 테스트")
    inner class RetrieveUserPointTest {
        @Nested
        @DisplayName("유저 포인트 조회 성공 테스트")
        inner class RetrieveUserPointSuccessTest {
            @Test
            @DisplayName("유저 아이디를 통해 해당 유저의 포인트를 조회할 수 있어야 한다.")
            fun retrieveUserPointTest() {
                // given
                val userId = 1L

                fakeUserPointQueryPort.singleUserPointFixture(userId)

                // when
                val actual: UserPointQueryUseCase.UserPointResponse =
                    pointService.retrieveUserPoint(userId)

                // then
                SoftAssertions.assertSoftly { softAssertions ->
                    softAssertions.assertThat(actual).isNotNull
                    softAssertions.assertThat(actual.id).isEqualTo(userId)
                }
            }
        }

        @Nested
        @DisplayName("유저 포인트 조회 실패 테스트")
        inner class RetrieveUserPointFailTest {
            @Test
            @DisplayName("존재하지 않는 유저 아이디로 포인트 조회 시 예외가 발생해야 한다.")
            fun retrieveUserPointTest() {
                // given
                val wrongUserId = 1L

                fakeUserPointQueryPort.singleUserPointFixture(2L)

                // when & then
                Assertions
                    .assertThatThrownBy {
                        pointService.retrieveUserPoint(wrongUserId)
                    }.isInstanceOf(CustomException::class.java)
                    .hasMessage("${ErrorCode.NOT_FOUND_USER_POINT.message} - $wrongUserId")
            }
        }
    }
}
