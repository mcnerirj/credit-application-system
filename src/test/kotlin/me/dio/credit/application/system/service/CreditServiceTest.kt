package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import me.dio.credit.application.system.enummeration.Status
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    private lateinit var creditRepository: CreditRepository

    @MockK
    private lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService

    @Test
    fun `should save credit`() {
        //given
        val fakeCredit = buildCredit()
        val fakeCustomer = buildCustomer()
        every { customerService.findById(any()) } returns fakeCustomer
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual = creditService.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)

        verify(exactly = 1) { customerService.findById(fakeCredit.customer?.id ?: -1) }
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `on save should throw invalid date`() {
        //given
        val fakeCredit = buildCredit(dayFirstInstallment = LocalDate.now().plusMonths(4))
        val fakeCustomer = buildCustomer()
        every { customerService.findById(any()) } returns fakeCustomer
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.save(fakeCredit) }.withMessage("Invalid Date")
    }

    @Test
    fun `on save should throw Id not found`() {
        //given
        val fakeCredit = buildCredit(dayFirstInstallment = LocalDate.now())
        every { customerService.findById(any()) } throws BusinessException("Id ${fakeCredit.customer?.id} not found")
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.save(fakeCredit) }.withMessage("Id ${fakeCredit.customer?.id} not found")
    }

    @Test
    fun `should find all credits of costumerId`() {
        //given
        val fakeList = listOf(buildCredit(), buildCredit())
        every { creditRepository.findByAllByCustomerId(any()) } returns fakeList
        //when
        val actual = creditService.findAllByCustomer(1L)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeList)

        verify(exactly = 1) { creditRepository.findByAllByCustomerId(1L) }
    }

    @Test
    fun `should find credit by code and customer id`() {
        //given
        val fakeCredit = buildCredit()
        every { creditRepository.findByCreditCode(any()) } returns fakeCredit
        //when
        val actual = creditService.findByCreditCode(1L, fakeCredit.creditCode)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)

        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCredit.creditCode) }
    }

    @Test
    fun `should throws credit code not found`() {
        //given
        val fakeCredit = buildCredit()
        every { creditRepository.findByCreditCode(any()) } returns null
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(1L, fakeCredit.creditCode) }
            .withMessage("Creditcode ${fakeCredit.creditCode} not found")
    }

    @Test
    fun `should throws Contact admin`() {
        //given
        val fakeCredit = buildCredit()
        every { creditRepository.findByCreditCode(any()) } returns fakeCredit
        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { creditService.findByCreditCode(2L, fakeCredit.creditCode) }.withMessage("Contact admin")
    }

    fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(2000),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 1,
        status: Status = Status.IN_PROGRESS,
        idCredit: Long? = 1L,
        customer: Customer = buildCustomer()
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = idCredit
    )

    fun buildCustomer(
        firstName: String = "Marcelo",
        lastName: String = "Neri",
        cpf: String = "34320550056",
        email: String = "teste@gmail.com",
        password: String = "1234",
        zipCode: String = "11122333",
        street: String = "Av Central",
        income: BigDecimal = BigDecimal.valueOf(7000),
        id: Long = 1L
    ) = Customer(
        firstName = firstName, lastName = lastName, cpf = cpf, email = email, password = password, address = Address(
            zipCode = zipCode,
            street = street,
        ), income = income, id = id
    )

}