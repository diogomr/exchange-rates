package com.rates.exchange.rest

import com.ninjasquad.springmockk.MockkBean
import com.rates.exchange.domain.ExchangeRate
import com.rates.exchange.usecases.GetAllExchangeRatesForDayUseCase
import com.rates.exchange.usecases.GetExchangeRateRangeUseCase
import com.rates.exchange.usecases.GetLatestExchangeRateUseCase
import com.rates.exchange.usecases.InsertExchangeRateUseCase
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

@WebMvcTest(ExchangeRatesController::class)
internal class ExchangeRatesControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var getLatestExchangeRateUseCase: GetLatestExchangeRateUseCase

    @MockkBean
    lateinit var getExchangeRateRangeUseCase: GetExchangeRateRangeUseCase

    @MockkBean
    lateinit var getAllExchangeRatesForDayUseCase: GetAllExchangeRatesForDayUseCase

    @MockkBean
    lateinit var insertExchangeRateUseCase: InsertExchangeRateUseCase

    @Test
    fun `getRange - when all fields are valid, then use case is called and response is 200 Ok`() {

        // Given
        val now = LocalDate.now()
        every { getExchangeRateRangeUseCase.get(any(), any(), any(), any()) } returns listOf(
            ExchangeRate(now, 1.0),
            ExchangeRate(now.plusDays(1), 2.0),
        )

        val base = "EUR"
        val currency = "CHF"
        val from = "2017-01-06"
        val to = "2017-01-07"

        // When
        mockMvc.get("/exchange-rates?base=$base&currency=$currency&from=$from&to=$to")
            // Then
            .andExpect {
                status { isOk() }
                content {
                    jsonPath("$[0].date") { value(now.toString()) }
                    jsonPath("$[0].rate") { value(1.0) }
                    jsonPath("$[1].date") { value(now.plusDays(1).toString()) }
                    jsonPath("$[1].rate") { value(2.0) }
                }
            }
    }

    @Test
    fun `getRange - when base currency is invalid, then 400 Bad request is returned`() {

        // Given
        val base = "INVALID"
        val currency = "CHF"
        val from = "2017-01-06"
        val to = "2017-01-07"

        // When
        mockMvc.get("/exchange-rates?base=$base&currency=$currency&from=$from&to=$to")
            // Then
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `getRange - when currency is invalid, then 400 Bad request is returned`() {

        // Given
        val base = "EUR"
        val currency = "INVALID"
        val from = "2017-01-06"
        val to = "2017-01-07"

        // When
        mockMvc.get("/exchange-rates?base=$base&currency=$currency&from=$from&to=$to")
            // Then
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `getRange - when date is not properly formatted, then 400 Bad request is returned`() {

        // Given
        val base = "EUR"
        val currency = "CHF"
        val from = "2017/01/06"
        val to = "2017-01-07"

        // When
        mockMvc.get("/exchange-rates?base=$base&currency=$currency&from=$from&to=$to")
            // Then
            .andExpect {
                status { isBadRequest() }
            }
    }
}
