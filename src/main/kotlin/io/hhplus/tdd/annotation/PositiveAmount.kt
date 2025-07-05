package io.hhplus.tdd.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [PositiveAmountValidator::class])
annotation class PositiveAmount(
    val message: String = "invalid positive amount",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
