package io.hhplus.tdd.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PositiveAmountValidator : ConstraintValidator<PositiveAmount, Long> {
    companion object {
        private const val USER_POINT_LIMIT = 1_000_000
    }

    override fun isValid(
        value: Long?,
        context: ConstraintValidatorContext?
    ): Boolean = value != null && value > 0 && value < USER_POINT_LIMIT
}
