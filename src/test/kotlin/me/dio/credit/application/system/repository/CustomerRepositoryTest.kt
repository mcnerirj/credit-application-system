package me.dio.credit.application.system.repository

import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {
    @Autowired lateinit var customerRepository: CustomerRepository
    @Autowired lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    @BeforeEach fun setup () {
        customer = testEntityManager.persist(buildCustomer())
    }

    @Test
    fun `should find customer by cpf`() {
        customer = customerRepository.findByIncome(2500.0)
        Assertions.assertThat(customer).isNotNull
    }


    private fun buildCustomer(
        firstName: String = "Marcelo",
        lastName: String = "Neri",
        cpf: String = "64953775090",
        email: String = "mcneri@gmail.com",
        password: String = "123456",
        zipCode: String = "123456",
        street: String = "Av Rio Branco",
        income: BigDecimal = BigDecimal.valueOf(2500.0),
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        income = income,
    )
}