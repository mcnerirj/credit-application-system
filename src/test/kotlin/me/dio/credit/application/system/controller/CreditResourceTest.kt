package me.dio.credit.application.system.controller

import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.dto.CreditView
import me.dio.credit.application.system.dto.CreditViewList
import me.dio.credit.application.system.dto.CustomerDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.service.impl.CreditService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import me.dio.credit.application.system.enummeration.Status
class CreditResourceTest {

    @Mock
    private lateinit var creditService: CreditService

    @InjectMocks
    private lateinit var creditResource: CreditResource

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    /*
    @Test
    fun saveCredit_shouldReturnCreatedStatus() {
        val creditDto = buildCreditDto()
        val credit =  buildCredit()
        credit.creditCode = UUID.randomUUID()
        credit.customer = null

        `when`(creditService.save(creditDto.toEntity())).thenReturn(credit)

        val response = creditResource.saveCredit(creditDto)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("Credit ${credit.creditCode} - Customer null saved!", response.body)
    }
*/
    @Test
    fun saveCredit_shouldReturnCreatedStatus() {
        val creditDto: CreditDto = buildCreditDto()
        val credit : Credit = buildCredit()
        credit.customer = null

        `when`(creditService.save(creditDto.toEntity())).thenReturn(credit)

        val response = creditResource.saveCredit(creditDto)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("Credit ${credit.creditCode} - Customer null saved!", response.body)
    }

    @Test
    fun findByAllCustomerId_shouldReturnListOfCreditViewList() {
        val customerId = 1L
        val credit1 = buildCredit()
        val credit2 = buildCredit()
        val creditList = listOf(credit1, credit2)
        val creditViewList = creditList.map { CreditViewList(it) }

        `when`(creditService.findAllByCustomer(customerId)).thenReturn(creditList)

        val response = creditResource.findByAllCustomerId(customerId)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(creditViewList, response.body)
    }

    @Test
    fun findByCreditCode_shouldReturnCreditView() {
        val customerId = 1L
        val creditCode = UUID.randomUUID()
        val credit = buildCredit(creditCode = creditCode)
        val creditView = CreditView(credit)

        `when`(creditService.findByCreditCode(customerId, creditCode)).thenReturn(credit)

        val response = creditResource.findByCreditCode(customerId, creditCode)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(creditView, response.body)
    }
    private fun buildCreditViewList(
        creditCode: UUID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        numberOfInstallment: Int = 10
    ) = CreditViewList(
        creditCode = creditCode,
        creditValue = creditValue,
        numberOfInstallment = numberOfInstallment
    )

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.ZERO,
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 0,
        status: Status = Status.IN_PROGRESS,
        customer: Customer? = null,
        id: Long? = null
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id
    )
    fun buildCreditDto(
        creditValue: BigDecimal =  BigDecimal.ZERO,
        dayFirstOfInstallment: LocalDate =LocalDate.now(),
        numberOfInstallments: Int = 15,
        customerId: Long = 1L
    )= CreditDto (
            creditValue = creditValue,
            dayFirstOfInstallment = dayFirstOfInstallment,
            numberOfInstallments = numberOfInstallments,
            customerId = customerId
    )

}