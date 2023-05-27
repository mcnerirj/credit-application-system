package me.dio.credit.application.system.validator

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import java.time.LocalDate


import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FutureDaysValidator::class])
@ReportAsSingleViolation
annotation class FutureDays(
    val daysAhead: Int,
    val message: String = "The date must be within {daysAhead} days in the future.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
class FutureDaysValidator : ConstraintValidator<FutureDays, LocalDate> {
    private var daysAhead: Int = 0

    override fun initialize(constraintAnnotation: FutureDays) {
        daysAhead = constraintAnnotation.daysAhead
    }

    override fun isValid(value: LocalDate?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true // A anotação @NotNull irá lidar com valores nulos
        }

        val currentDate = LocalDate.now()
        val maxAllowedDate = currentDate.plusDays(daysAhead.toLong())

        return value.isAfter(currentDate) && value.isBefore(maxAllowedDate)
    }
}