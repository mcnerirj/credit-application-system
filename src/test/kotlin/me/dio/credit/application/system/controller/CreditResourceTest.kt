package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CustomerService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerService: CustomerService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    lateinit var savedCustomer: Customer

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeAll
    fun setupAll() {
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        savedCustomer = customerService.save(buildCustomer())
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()

    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
    }

    @AfterAll
    fun tearDownAll() {
        customerService.delete(1L)
    }

    @Test
    fun `should create credit and return 201 status`() {
        //given
        val creditDto = builderCreditDto()
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("teste@teste.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(10.00))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should create and return 400 status`() {
        //given
        val creditDto = builderCreditDto(numberOfInstallments = 15)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400)).andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            ).andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").value("must be less than or equal to 48"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should get credit by credit code and customer id  and return 200 status`() {
        //given
        val creditDto = builderCreditDto()
        val credit = creditRepository.save(creditDto.toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=${savedCustomer.id}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("teste@teste.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(10.00))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should get credit by credit code and wrong customer id  and return 400 status`() {
        //given
        val creditDto = builderCreditDto()
        val credit = creditRepository.save(creditDto.toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=2")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)

            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class java.lang.IllegalArgumentException")
            ).andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").value("Contact admin"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should get credit by wrong credit code and  customer id  and return 400 status`() {
        //given
        val creditDto = builderCreditDto()
        creditRepository.save(creditDto.toEntity())
        val wrongId = "111222"
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$wrongId?customerId=2")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)

            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class java.lang.IllegalArgumentException")
            ).andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").value("Invalid UUID string: $wrongId"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should get credit by customer id  and return 200 status`() {
        //given
        val creditDto = builderCreditDto()
        val credit = creditRepository.save(creditDto.toEntity())
        creditRepository.save(
            builderCreditDto(
                numberOfInstallments = 2,
                creditValue = BigDecimal.valueOf(7)
            ).toEntity()
        )
        creditRepository.save(
            builderCreditDto(
                numberOfInstallments = 3,
                creditValue = BigDecimal.valueOf(1000)
            ).toEntity()
        )
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=${savedCustomer.id}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditCode").value(credit.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditValue").value(10.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].numberOfInstallments").value(5))
            .andDo(MockMvcResultHandlers.print())
    }


    @Test
    fun `should get credit by wrong customer id and return 400 status`() {
        //given
        val creditDto = builderCreditDto()
        val wrongId = 2L
        creditRepository.save(creditDto.toEntity())
        creditRepository.save(
            builderCreditDto(
                numberOfInstallments = 2,
                creditValue = BigDecimal.valueOf(7)
            ).toEntity()
        )
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=${wrongId}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
            .andDo(MockMvcResultHandlers.print())
    }

    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(200),
        dayFirstOfInstallment: LocalDate = LocalDate.of(2023, Month.JUNE, 15),
        numberOfInstallments: Int = 10,
        customerId: Long = 1L
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun buildCustomer(
        firstName: String = "MARCELO",
        lastName: String = "NERI",
        cpf: String = "34320550056",
        email: String = "teste@gmail.com",
        password: String = "12341234",
        zipCode: String = "12345678",
        street: String = "Av Rio Branco",
        income: BigDecimal = BigDecimal.valueOf(5000),
        id: Long = 1L
    ) = Customer(
        firstName = firstName, lastName = lastName, cpf = cpf, email = email, password = password, address = Address(
            zipCode = zipCode,
            street = street,
        ), income = income, id = id
    )

}