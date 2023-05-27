package me.dio.credit.application.system.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.validator.FutureDays
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Invalid input")  val creditValue: BigDecimal,
    @field:Future @field:FutureDays(daysAhead = 90) val dayFirstOfInstallment: LocalDate,
    @field:Min(value = 1) @field:Max(value = 48, message = "Max number Installments is 48")
    @field:NotNull(message = "Invalid input")  val numberOfInstallments: Int,
    @field:NotNull(message = "Invalid input")  val customerId: Long
) {

    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstOfInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )

}
